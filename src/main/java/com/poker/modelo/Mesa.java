package com.poker.modelo;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Mesa {
    public static final int MAX_JUGADORES = 4;

    private List<Jugador> jugadores;
    private Crupier crupier;
    private CartasComunitarias comunitarias;
    @Setter
    private int posicionBoton;

    public Mesa(Crupier crupier) {
        this.crupier = crupier;
        this.jugadores = new ArrayList<>();
        this.comunitarias = new CartasComunitarias();
        this.posicionBoton = 0;
    }

    public void agregarJugador(Jugador jugador) {
        // No se agregan jugadores null ni más de 4
        if (jugador == null || jugadores.size() >= MAX_JUGADORES)
            return;
        jugadores.add(jugador);
    }

    public void rotarBoton() {
        if (jugadores.isEmpty())
            return;
        // El botón pasa al siguiente jugador que no esté eliminado
        posicionBoton = siguientePosicion(posicionBoton);
    }

    public Jugador jugadorCiegaPequena() {
        if (jugadores.isEmpty())
            return null;
        // Con 2 jugadores el botón paga la ciega pequeña
        if (contarEnJuego() == 2) {
            return jugadores.get(posicionBoton);
        }
        // Si no, la paga el jugador a la izquierda del botón
        return jugadores.get(siguientePosicion(posicionBoton));
    }

    public Jugador jugadorCiegaGrande() {
        Jugador pequena = jugadorCiegaPequena();
        if (pequena == null)
            return null;
        // La ciega grande es el jugador a la izquierda de la pequeña
        return jugadores.get(siguientePosicion(jugadores.indexOf(pequena)));
    }

    public List<Jugador> jugadoresActivos() {
        // Siguen en la mano los jugadores activos o en all-in
        List<Jugador> activos = new ArrayList<>();
        for (Jugador jugador : jugadores) {
            if (jugador.getEstado() == EstadoJugador.ACTIVO || jugador.getEstado() == EstadoJugador.ALL_IN) {
                activos.add(jugador);
            }
        }
        return activos;
    }

    private int siguientePosicion(int desde) {
        // Recorrer la mesa desde la posición dada saltando a los eliminados
        for (int i = 1; i <= jugadores.size(); i++) {
            int posicion = (desde + i) % jugadores.size();
            if (jugadores.get(posicion).getEstado() != EstadoJugador.ELIMINADO) {
                return posicion;
            }
        }
        return desde;
    }

    private int contarEnJuego() {
        // Contar los jugadores que no están eliminados
        int cantidad = 0;
        for (Jugador jugador : jugadores) {
            if (jugador.getEstado() != EstadoJugador.ELIMINADO) {
                cantidad++;
            }
        }
        return cantidad;
    }
}
