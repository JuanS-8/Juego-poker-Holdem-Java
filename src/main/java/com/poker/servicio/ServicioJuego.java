package com.poker.servicio;

import com.poker.bot.FabricaEstrategiaBot;
import com.poker.dao.ExcepcionAccesoDatos;
import com.poker.dao.HistorialManoDAO;
import com.poker.dao.JugadorDAO;
import com.poker.dao.PartidaDAO;
import com.poker.dto.*;
import com.poker.mapeador.CartaMapper;
import com.poker.mapeador.JugadorMapper;
import com.poker.mapeador.MesaMapper;
import com.poker.modelo.*;
import com.poker.vista.ProveedorAccion;

import java.time.LocalDateTime;
import java.util.*;

public class ServicioJuego {
    private Mesa mesa;
    private ConfiguracionPartida configuracion;
    private ServicioApuestas servicioApuestas;
    private ServicioShowdown servicioShowdown;
    private GestorBote gestorBote;
    private JugadorDAO jugadorDAO;
    private PartidaDAO partidaDAO;
    private HistorialManoDAO historialDAO;
    private EscuchaEventosJuego escucha;
    private ProveedorAccion proveedorAccion;
    private String idPartida;
    private int numeroMano;

    public ServicioJuego(Mesa mesa, ServicioApuestas servicioApuestas, ServicioShowdown servicioShowdown, GestorBote gestorBote, JugadorDAO jugadorDAO,
            PartidaDAO partidaDAO, HistorialManoDAO historialDAO, EscuchaEventosJuego escucha, ProveedorAccion proveedorAccion) {
        this.mesa = mesa;
        this.servicioApuestas = servicioApuestas;
        this.servicioShowdown = servicioShowdown;
        this.gestorBote = gestorBote;
        this.jugadorDAO = jugadorDAO;
        this.partidaDAO = partidaDAO;
        this.historialDAO = historialDAO;
        this.escucha = escucha;
        this.proveedorAccion = proveedorAccion;
    }

    public void iniciarPartida(ConfiguracionPartida configuracion) {
        if (configuracion == null)
            return;

        // Completar los datos vacíos con los valores por defecto
        String nombre = configuracion.nombreJugador();
        if (nombre == null || nombre.isBlank()) {
            nombre = "Jugador";
        }
        int fichas = configuracion.fichasIniciales() > 0 ? configuracion.fichasIniciales() : 1000;
        int pequena = configuracion.ciegaPequena() > 0 ? configuracion.ciegaPequena() : 10;
        int grande = configuracion.ciegaGrande() > pequena ? configuracion.ciegaGrande() : pequena * 2;
        this.configuracion = new ConfiguracionPartida(nombre.trim(), fichas, pequena, grande, configuracion.dificultad());

        // Sentar 1 humano y 3 bots
        mesa.getJugadores().clear();
        mesa.agregarJugador(new JugadorHumano(UUID.randomUUID().toString(), this.configuracion.nombreJugador(), fichas,
                proveedorAccion));
        FabricaEstrategiaBot fabrica = new FabricaEstrategiaBot();
        String[] nombresBots = {"Bot Ana", "Bot Luis", "Bot Sofia"};
        for (String nombreBot : nombresBots) {
            mesa.agregarJugador(new JugadorBot(UUID.randomUUID().toString(), nombreBot, fichas,
                    fabrica.crear(this.configuracion.dificultad())));
        }

        // Registrar los jugadores en la base de datos
        for (Jugador jugador : mesa.getJugadores()) {
            registrarJugador(jugador);
        }

        // Datos de la nueva partida
        mesa.setPosicionBoton(0);
        idPartida = UUID.randomUUID().toString();
        numeroMano = 1;
    }

    public HistorialManoDTO jugarMano() {
        if (configuracion == null || partidaTerminada())
            return null;

        // Preparar la mano: jugadores, cartas comunitarias, bote y registro
        for (Jugador jugador : mesa.getJugadores()) {
            jugador.limpiarMano();
        }
        mesa.getComunitarias().limpiar();
        gestorBote.reiniciar();
        servicioApuestas.limpiarRegistro();

        // Barajar y repartir desde el jugador de la ciega pequeña
        List<Jugador> orden = new ArrayList<>();
        List<Jugador> jugadores = mesa.getJugadores();
        int inicio = jugadores.indexOf(mesa.jugadorCiegaPequena());
        for (int i = 0; i < jugadores.size(); i++) {
            Jugador jugador = jugadores.get((inicio + i) % jugadores.size());
            if (jugador.getEstado() != EstadoJugador.ELIMINADO) {
                orden.add(jugador);
            }
        }
        mesa.getCrupier().prepararBaraja();
        mesa.getCrupier().repartirCartasPropias(orden);

        // Cobrar las ciegas
        servicioApuestas.cobrarCiegas(mesa, configuracion.ciegaPequena(), configuracion.ciegaGrande(), gestorBote);

        // Jugar las 4 rondas de apuestas
        FaseJuego[] fases = {FaseJuego.PRE_FLOP, FaseJuego.FLOP, FaseJuego.TURN, FaseJuego.RIVER};
        for (FaseJuego fase : fases) {
            // Repartir las cartas comunitarias de la fase
            if (fase == FaseJuego.FLOP) {
                mesa.getCrupier().repartirFlop(mesa.getComunitarias());
            } else if (fase == FaseJuego.TURN) {
                mesa.getCrupier().repartirTurn(mesa.getComunitarias());
            } else if (fase == FaseJuego.RIVER) {
                mesa.getCrupier().repartirRiver(mesa.getComunitarias());
            }
            // Las apuestas de la ronda anterior ya están en el bote
            if (fase != FaseJuego.PRE_FLOP) {
                for (Jugador jugador : jugadores) {
                    jugador.setApuestaActual(0);
                }
            }
            // Mostrar la mesa y jugar la ronda
            avisarCambioFase(fase, false);
            if (!servicioApuestas.ejecutarRonda(mesa, fase, gestorBote))
                break;
        }

        // Repartir los botes y pagar a los ganadores
        int bote = gestorBote.total();
        boolean hayShowdown = mesa.jugadoresActivos().size() > 1;
        Map<Jugador, Integer> ganancias = servicioShowdown.repartirBotes(gestorBote.calcularBotes(), mesa.getComunitarias());
        for (Jugador ganador : ganancias.keySet()) {
            ganador.setFichas(ganador.getFichas() + ganancias.get(ganador));
        }
        // En el showdown se muestran las cartas de los bots
        if (hayShowdown) {
            avisarCambioFase(FaseJuego.SHOWDOWN, true);
        }
        gestorBote.reiniciar();

        // Eliminar a los jugadores sin fichas
        for (Jugador jugador : jugadores) {
            if (jugador.getFichas() == 0) {
                jugador.setEstado(EstadoJugador.ELIMINADO);
            }
        }

        // Guardar estadísticas e historial
        actualizarEstadisticas(ganancias);
        HistorialManoDTO historial = crearHistorial(ganancias, bote);
        try {
            historialDAO.guardar(historial);
        } catch (ExcepcionAccesoDatos excepcion) {
            System.err.println("No se pudo guardar el historial de la mano: " + excepcion.getMessage());
        }

        // Avisar el resultado y preparar la siguiente mano
        if (escucha != null) {
            escucha.alTerminarMano(historial);
        }
        mesa.rotarBoton();
        numeroMano++;
        return historial;
    }

    public boolean partidaTerminada() {
        // Termina si el humano no tiene fichas o si queda un solo jugador con fichas
        int conFichas = 0;
        for (Jugador jugador : mesa.getJugadores()) {
            if (jugador.getFichas() > 0) {
                conFichas++;
            } else if (jugador instanceof JugadorHumano) {
                return true;
            }
        }
        return conFichas <= 1;
    }

    public void guardarPartida() {
        if (configuracion == null)
            return;

        // Convertir los jugadores en DTO
        JugadorMapper mapper = new JugadorMapper();
        List<JugadorDTO> jugadores = new ArrayList<>();
        for (Jugador jugador : mesa.getJugadores()) {
            jugadores.add(mapper.aDTO(jugador));
        }
        EstadoPartidaDTO estado = new EstadoPartidaDTO(idPartida, jugadores, mesa.getPosicionBoton(),
                configuracion.ciegaPequena(), configuracion.ciegaGrande(), numeroMano);

        // Guardar o actualizar la partida
        try {
            if (partidaDAO.buscarPorId(idPartida).isPresent()) {
                partidaDAO.actualizar(estado);
            } else {
                partidaDAO.guardar(estado);
            }
        } catch (ExcepcionAccesoDatos excepcion) {
            System.err.println("No se pudo guardar la partida: " + excepcion.getMessage());
        }
    }

    public void cargarPartida(String idPartida) {
        // Buscar la partida
        Optional<EstadoPartidaDTO> guardada;
        try {
            if (idPartida == null) {
                guardada = partidaDAO.buscarUltimaGuardada();
            } else {
                guardada = partidaDAO.buscarPorId(idPartida);
            }
        } catch (ExcepcionAccesoDatos excepcion) {
            System.err.println("No se pudo cargar la partida: " + excepcion.getMessage());
            return;
        }
        if (guardada.isEmpty())
            return;
        EstadoPartidaDTO estado = guardada.get();

        // Volver a sentar a los jugadores con sus fichas
        JugadorMapper mapper = new JugadorMapper();
        FabricaEstrategiaBot fabrica = new FabricaEstrategiaBot();
        String nombreHumano = "Jugador";
        mesa.getJugadores().clear();
        for (JugadorDTO dto : estado.jugadores()) {
            Jugador jugador = mapper.aEntidad(dto);
            // Asignar de nuevo la estrategia y el proveedor
            if (jugador instanceof JugadorBot) {
                ((JugadorBot) jugador).setEstrategia(fabrica.crear(null));
            } else {
                ((JugadorHumano) jugador).setProveedor(proveedorAccion);
                nombreHumano = jugador.getNombre();
            }
            mesa.agregarJugador(jugador);
        }

        // Recuperar los datos de la partida
        mesa.setPosicionBoton(estado.posicionBoton());
        this.configuracion = new ConfiguracionPartida(nombreHumano, 0, estado.ciegaPequena(), estado.ciegaGrande(), null);
        this.idPartida = estado.idPartida();
        this.numeroMano = estado.numeroMano();
    }

    public JugadorDTO consultarEstadisticas(String nombre) {
        try {
            return jugadorDAO.buscarPorNombre(nombre).orElse(null);
        } catch (ExcepcionAccesoDatos excepcion) {
            System.err.println("No se pudieron consultar las estadísticas: " + excepcion.getMessage());
            return null;
        }
    }

    public MesaDTO obtenerMesa() {
        return new MesaMapper().aDTO(mesa, null, gestorBote.total(), false);
    }

    private void registrarJugador(Jugador jugador) {
        try {
            // Si el nombre ya existe se usa ese jugador
            Optional<JugadorDTO> existente = jugadorDAO.buscarPorNombre(jugador.getNombre());
            if (existente.isPresent()) {
                jugador.setId(existente.get().id());
            } else {
                // Si no existe se guarda
                jugadorDAO.guardar(new JugadorMapper().aDTO(jugador));
            }
        } catch (ExcepcionAccesoDatos excepcion) {
            System.err.println("No se pudo registrar al jugador " + jugador.getNombre() + ": " + excepcion.getMessage());
        }
    }

    private void avisarCambioFase(FaseJuego fase, boolean mostrarBots) {
        if (escucha != null) {
            escucha.alCambiarFase(fase, new MesaMapper().aDTO(mesa, fase, gestorBote.total(), mostrarBots));
        }
    }

    private void actualizarEstadisticas(Map<Jugador, Integer> ganancias) {
        for (Jugador jugador : mesa.getJugadores()) {
            // Solo cuentan los que jugaron la mano
            if (jugador.getCartas().isEmpty())
                continue;
            try {
                Optional<JugadorDTO> guardado = jugadorDAO.buscarPorNombre(jugador.getNombre());
                if (guardado.isEmpty())
                    continue;
                // Sumar manos jugadas, manos ganadas y máximo de fichas
                JugadorDTO dto = guardado.get();
                int ganadas = dto.manosGanadas();
                if (ganancias.containsKey(jugador)) {
                    ganadas++;
                }
                int maximo = Math.max(dto.maximoFichas(), jugador.getFichas());
                jugadorDAO.actualizar(new JugadorDTO(dto.id(), dto.nombre(), jugador.getFichas(), dto.esBot(),
                        dto.manosJugadas() + 1, ganadas, maximo));
            } catch (ExcepcionAccesoDatos excepcion) {
                System.err.println("No se pudieron actualizar las estadísticas: " + excepcion.getMessage());
            }
        }
    }

    private HistorialManoDTO crearHistorial(Map<Jugador, Integer> ganancias, int bote) {
        // Convertir las cartas comunitarias
        CartaMapper cartaMapper = new CartaMapper();
        List<CartaDTO> comunitarias = new ArrayList<>();
        for (Carta carta : mesa.getComunitarias().getCartas()) {
            comunitarias.add(cartaMapper.aDTO(carta));
        }
        // Las ganancias se guardan por nombre del jugador
        Map<String, Integer> gananciasPorNombre = new LinkedHashMap<>();
        for (Jugador ganador : ganancias.keySet()) {
            gananciasPorNombre.put(ganador.getNombre(), ganancias.get(ganador));
        }
        // Crear el historial con las acciones de la mano
        return new HistorialManoDTO(UUID.randomUUID().toString(), idPartida, numeroMano, comunitarias,
                servicioApuestas.obtenerRegistro(), gananciasPorNombre, bote, LocalDateTime.now());
    }
}
