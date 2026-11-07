package com.poker.servicio;

import com.poker.dto.AccionDTO;
import com.poker.excepcion.ExcepcionAccionInvalida;
import com.poker.modelo.*;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class ServicioApuestas {
    private ValidadorAccion validador;
    private EscuchaEventosJuego escucha;
    private int ciegaGrande;
    private List<AccionDTO> registro;

    public ServicioApuestas(ValidadorAccion validador, EscuchaEventosJuego escucha) {
        this.validador = validador;
        this.escucha = escucha;
        this.registro = new ArrayList<>();
    }

    public void cobrarCiegas(Mesa mesa, int pequena, int grande, GestorBote gestor) {
        // Guardar la ciega grande
        this.ciegaGrande = grande;
        Jugador jugadorPequena = mesa.jugadorCiegaPequena();
        Jugador jugadorGrande = mesa.jugadorCiegaGrande();
        if (jugadorPequena == null || jugadorGrande == null)
            return;
        // Cobrar las ciegas
        gestor.agregarAporte(jugadorPequena, jugadorPequena.apostar(pequena));
        gestor.agregarAporte(jugadorGrande, jugadorGrande.apostar(grande));
    }

    public boolean ejecutarRonda(Mesa mesa, FaseJuego fase, GestorBote gestor) {
        List<Jugador> jugadores = mesa.getJugadores();

        // Si solo queda un jugador en la mano no hay ronda
        if (mesa.jugadoresActivos().size() <= 1)
            return false;

        // Después del pre-flop cada ronda empieza sin apuestas
        if (fase != FaseJuego.PRE_FLOP) {
            for (Jugador jugador : jugadores) {
                jugador.setApuestaActual(0);
            }
        }
        int apuestaMaxima = 0;
        for (Jugador jugador : jugadores) {
            apuestaMaxima = Math.max(apuestaMaxima, jugador.getApuestaActual());
        }
        int subidaMinima = Math.max(ciegaGrande, 1);

        // Todos los jugadores activos deben hablar al menos una vez
        Set<Jugador> pendientes = new LinkedHashSet<>();
        for (Jugador jugador : jugadores) {
            if (jugador.getEstado() == EstadoJugador.ACTIVO) {
                pendientes.add(jugador);
            }
        }
        // Si solo uno puede actuar y ya igualó, no hace falta preguntarle
        if (pendientes.size() == 1 && pendientes.iterator().next().getApuestaActual() >= apuestaMaxima)
            return true;

        // Buscar el primer jugador en hablar
        int posicion;
        if (fase == FaseJuego.PRE_FLOP) {
            posicion = (jugadores.indexOf(mesa.jugadorCiegaGrande()) + 1) % jugadores.size();
        } else {
            posicion = (mesa.getPosicionBoton() + 1) % jugadores.size();
        }

        // Dar turnos hasta que nadie tenga que hablar
        while (!pendientes.isEmpty() && mesa.jugadoresActivos().size() > 1) {
            Jugador jugador = jugadores.get(posicion);
            posicion = (posicion + 1) % jugadores.size();
            if (!pendientes.contains(jugador))
                continue;
            pendientes.remove(jugador);
            if (jugador.getEstado() != EstadoJugador.ACTIVO)
                continue;

            // Armar el contexto para que el jugador decida
            int porIgualar = apuestaMaxima - jugador.getApuestaActual();
            Set<TipoAccion> validas = validador.accionesValidas(jugador, porIgualar, subidaMinima);
            ContextoDecision contexto = new ContextoDecision(new ArrayList<>(jugador.getCartas()),
                    new ArrayList<>(mesa.getComunitarias().getCartas()), porIgualar, subidaMinima, gestor.total(), jugador.getFichas(), fase, validas);

            // Pedir la acción
            Accion accion = null;
            for (int intento = 0; intento < 3 && accion == null; intento++) {
                Accion propuesta = jugador.decidirAccion(contexto);
                try {
                    validador.validar(propuesta, jugador, porIgualar, subidaMinima);
                    accion = propuesta;
                } catch (ExcepcionAccionInvalida excepcion) {
                    System.err.println("Accion invalida de " + jugador.getNombre() + ": " + excepcion.getMessage());
                }
            }
            if (accion == null) {
                if (validas.contains(TipoAccion.PASAR)) {
                    accion = new Accion(TipoAccion.PASAR, 0);
                } else {
                    accion = new Accion(TipoAccion.RETIRARSE, 0);
                }
            }

            // Aplicar la acción
            int apostado = 0;
            if (accion.tipo() == TipoAccion.RETIRARSE) {
                jugador.retirarse();
            } else if (accion.tipo() == TipoAccion.IGUALAR) {
                apostado = jugador.apostar(porIgualar);
            } else if (accion.tipo() == TipoAccion.APOSTAR || accion.tipo() == TipoAccion.SUBIR) {
                apostado = jugador.apostar(accion.monto());
            } else if (accion.tipo() == TipoAccion.ALL_IN) {
                apostado = jugador.apostar(jugador.getFichas());
            }
            gestor.agregarAporte(jugador, apostado);

            // Si subió la apuesta, los demás activos deben volver a hablar
            if (jugador.getApuestaActual() > apuestaMaxima) {
                subidaMinima = Math.max(subidaMinima, jugador.getApuestaActual() - apuestaMaxima);
                apuestaMaxima = jugador.getApuestaActual();
                for (Jugador otro : jugadores) {
                    if (otro != jugador && otro.getEstado() == EstadoJugador.ACTIVO) {
                        pendientes.add(otro);
                    }
                }
            }

            // Guardar la acción en el registro y avisar a la vista
            registro.add(new AccionDTO(jugador.getId(), fase.name(), accion.tipo().name(), apostado));
            if (escucha != null) {
                escucha.alRealizarAccion(jugador, new Accion(accion.tipo(), apostado));
            }
        }

        // La mano sigue si quedan al menos 2 jugadores
        return mesa.jugadoresActivos().size() > 1;
    }

    public List<AccionDTO> obtenerRegistro() {
        return new ArrayList<>(registro);
    }

    public void limpiarRegistro() {
        registro.clear();
    }
}
