package com.poker.modelo;

import lombok.Getter;

import java.util.List;

@Getter
public class Crupier {
    private String nombre;
    private Baraja baraja;

    public Crupier(String nombre, Baraja baraja) {
        this.nombre = nombre;
        this.baraja = baraja;
    }

    public void prepararBaraja() {
        // Volver a tener las 52 cartas
        baraja.reiniciar();
        // Mezclarlas
        baraja.barajar();
    }

    public void repartirCartasPropias(List<Jugador> jugadores) {
        if (jugadores == null)
            return;
        // Dos vueltas a la mesa, una carta por jugador en cada vuelta
        for (int vuelta = 0; vuelta < 2; vuelta++) {
            for (Jugador jugador : jugadores) {
                jugador.recibirCarta(baraja.robar());
            }
        }
    }

    public void repartirFlop(CartasComunitarias comunitarias) {
        // Quemar una carta
        baraja.quemar();
        // Repartir 3 cartas comunitarias
        for (int i = 0; i < 3; i++) {
            comunitarias.agregar(baraja.robar());
        }
    }

    public void repartirTurn(CartasComunitarias comunitarias) {
        // Quemar una carta y repartir 1 comunitaria
        baraja.quemar();
        comunitarias.agregar(baraja.robar());
    }

    public void repartirRiver(CartasComunitarias comunitarias) {
        // Quemar una carta y repartir 1 comunitaria
        baraja.quemar();
        comunitarias.agregar(baraja.robar());
    }
}
