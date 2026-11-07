package com.poker.vista;

import com.poker.bot.DificultadBot;
import com.poker.dto.AsientoDTO;
import com.poker.dto.CartaDTO;
import com.poker.dto.HistorialManoDTO;
import com.poker.dto.MesaDTO;
import com.poker.modelo.*;
import com.poker.servicio.ConfiguracionPartida;
import com.poker.servicio.EscuchaEventosJuego;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class VistaConsola implements VistaJuego, ProveedorAccion, EscuchaEventosJuego {
    private Scanner lector;

    public VistaConsola() {
        this.lector = new Scanner(System.in);
    }

    public VistaConsola(Scanner lector) {
        this.lector = lector;
    }

    @Override
    public ConfiguracionPartida pedirConfiguracion() {
        // Título
        System.out.println("=========================================");
        System.out.println("        PÓKER TEXAS HOLD'EM              ");
        System.out.println("=========================================");
        // Pedir los datos
        System.out.print("Nombre del jugador [Jugador]: ");
        String nombre = lector.hasNextLine() ? lector.nextLine().trim() : "";
        int fichas = leerEntero("Fichas iniciales [1000]: ", 1000);
        int pequena = leerEntero("Ciega pequeña [10]: ", 10);
        int grande = leerEntero("Ciega grande [20]: ", 20);
        // Elegir la dificultad de los bots
        System.out.println("Dificultad de los bots: 1. Fácil  2. Medio  3. Difícil");
        int opcion = leerEntero("Opción [2]: ", 2);
        DificultadBot dificultad = DificultadBot.MEDIO;
        if (opcion == 1) {
            dificultad = DificultadBot.FACIL;
        } else if (opcion == 3) {
            dificultad = DificultadBot.DIFICIL;
        }
        return new ConfiguracionPartida(nombre, fichas, pequena, grande, dificultad);
    }

    @Override
    public void mostrarMesa(MesaDTO mesa) {
        if (mesa == null)
            return;
        // Fase, bote y cartas comunitarias
        System.out.println("-----------------------------------------");
        System.out.println(" Fase: " + mesa.fase() + "   Bote: " + mesa.bote());
        System.out.println(" Mesa: " + textoCartas(mesa.comunitarias()));
        // Un renglón por jugador
        for (AsientoDTO asiento : mesa.asientos()) {
            String cartas = "[? ?]";
            if (!asiento.cartasVisibles().isEmpty()) {
                cartas = textoCartas(asiento.cartasVisibles());
            }
            System.out.println(String.format("   %-10s fichas: %5d  apuesta: %4d  %-9s %s", asiento.nombre(),
                    asiento.fichas(), asiento.apuestaActual(), asiento.estado(), cartas));
        }
        System.out.println("-----------------------------------------");
    }

    @Override
    public void mostrarMensaje(String mensaje) {
        System.out.println(mensaje);
    }

    @Override
    public void mostrarResultado(HistorialManoDTO historial) {
        if (historial == null)
            return;
        System.out.println("=========== Resultado de la mano " + historial.numeroMano() + " ===========");
        for (String nombre : historial.ganancias().keySet()) {
            System.out.println("   " + nombre + " gana " + historial.ganancias().get(nombre) + " fichas");
        }
    }

    @Override
    public boolean preguntarContinuar() {
        return preguntarSiNo("¿Jugar otra mano? (s/n): ");
    }

    @Override
    public boolean preguntarGuardar() {
        return preguntarSiNo("¿Guardar la partida antes de salir? (s/n): ");
    }

    @Override
    public boolean preguntarCargarPartida() {
        return preguntarSiNo("¿Continuar la última partida guardada? (s/n): ");
    }

    @Override
    public Accion pedirAccion(ContextoDecision contexto) {
        // Mostrar las cartas y la situación del jugador
        System.out.println();
        System.out.println(">>> Tu turno. Tus cartas: " + contexto.cartasPropias() + "   Fichas: " + contexto.fichas()
                + "   Por igualar: " + contexto.porIgualar());
        List<TipoAccion> opciones = new ArrayList<>(contexto.accionesValidas());
        while (true) {
            // Mostrar solo las acciones válidas
            for (int i = 0; i < opciones.size(); i++) {
                System.out.println("   " + (i + 1) + ". " + opciones.get(i));
            }
            int opcion = leerEntero("Elige una opción: ", 0);
            if (opcion < 1 || opcion > opciones.size()) {
                System.out.println("Opción no válida.");
                continue;
            }
            TipoAccion tipo = opciones.get(opcion - 1);

            // Acciones que no piden monto
            if (tipo == TipoAccion.IGUALAR)
                return new Accion(tipo, contexto.porIgualar());
            if (tipo == TipoAccion.ALL_IN)
                return new Accion(tipo, contexto.fichas());
            if (tipo == TipoAccion.PASAR || tipo == TipoAccion.RETIRARSE)
                return new Accion(tipo, 0);

            // Apostar o subir: pedir el monto y validarlo
            int minimo = contexto.subidaMinima();
            if (tipo == TipoAccion.SUBIR) {
                minimo = contexto.porIgualar() + contexto.subidaMinima();
            }
            int monto = leerEntero("¿Cuántas fichas? (mínimo " + minimo + ", máximo " + contexto.fichas() + "): ", 0);
            if (monto < minimo) {
                System.out.println("El mínimo es " + minimo + " fichas.");
            } else if (monto > contexto.fichas()) {
                System.out.println("No tienes fichas suficientes, puedes ir all-in con " + contexto.fichas() + " fichas.");
            } else {
                return new Accion(tipo, monto);
            }
        }
    }

    @Override
    public void alRealizarAccion(Jugador jugador, Accion accion) {
        String monto = "";
        if (accion.monto() > 0) {
            monto = " " + accion.monto();
        }
        System.out.println("   " + jugador.getNombre() + ": " + accion.tipo() + monto);
    }

    @Override
    public void alCambiarFase(FaseJuego fase, MesaDTO mesa) {
        System.out.println();
        System.out.println("============== " + fase + " ==============");
        mostrarMesa(mesa);
    }

    @Override
    public void alTerminarMano(HistorialManoDTO historial) {
        mostrarResultado(historial);
    }

    private int leerEntero(String mensaje, int porDefecto) {
        // Leer la línea
        System.out.print(mensaje);
        String texto = lector.hasNextLine() ? lector.nextLine().trim() : "";
        if (texto.isEmpty())
            return porDefecto;
        // Convertir a número
        try {
            return Integer.parseInt(texto);
        } catch (NumberFormatException numberFormatException) {
            System.out.println("Debe ingresar un número.");
            return porDefecto;
        }
    }

    private boolean preguntarSiNo(String mensaje) {
        System.out.print(mensaje);
        String texto = lector.hasNextLine() ? lector.nextLine().trim().toLowerCase() : "n";
        return texto.startsWith("s");
    }

    private String textoCartas(List<CartaDTO> cartas) {
        // Cada carta como valor y símbolo del palo
        String texto = "[";
        for (CartaDTO carta : cartas) {
            String simbolo = "♠";
            if (carta.palo().equals("CORAZONES")) {
                simbolo = "♥";
            } else if (carta.palo().equals("DIAMANTES")) {
                simbolo = "♦";
            } else if (carta.palo().equals("TREBOLES")) {
                simbolo = "♣";
            }
            texto = texto + " " + carta.valor() + simbolo;
        }
        return texto + " ]";
    }
}
