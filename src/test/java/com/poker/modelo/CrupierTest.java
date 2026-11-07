package com.poker.modelo;

import com.poker.dobles.BarajadorOrdenFijo;
import com.poker.dobles.JugadorSimulado;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CrupierTest {
    private Crupier crupier;
    private CartasComunitarias comunitarias;

    @BeforeEach
    void setUp() {
        crupier = new Crupier("Crupier", new Baraja(new BarajadorOrdenFijo()));
        comunitarias = new CartasComunitarias();
    }

    // Preparar la baraja deja 52 cartas
    @Test
    void prepararBaraja() {
        crupier.getBaraja().robar();
        crupier.prepararBaraja();
        assertEquals(52, crupier.getBaraja().getCartas().size());
    }

    // Reparte 2 cartas a cada jugador, de una en una
    @Test
    void repartirCartasPropias() {
        Carta primera = new Carta(Valor.AS, Palo.PICAS);
        Carta segunda = new Carta(Valor.REY, Palo.PICAS);
        Carta tercera = new Carta(Valor.REINA, Palo.PICAS);
        Carta cuarta = new Carta(Valor.JOTA, Palo.PICAS);
        Crupier ordenado = new Crupier("Crupier", new Baraja(new BarajadorOrdenFijo(List.of(primera, segunda, tercera, cuarta))));
        ordenado.prepararBaraja();
        Jugador ana = new JugadorSimulado("Ana", 100);
        Jugador luis = new JugadorSimulado("Luis", 100);
        ordenado.repartirCartasPropias(List.of(ana, luis));
        assertEquals(List.of(primera, tercera), ana.getCartas(), "Ana recibe la 1a y la 3a carta");
        assertEquals(List.of(segunda, cuarta), luis.getCartas(), "Luis recibe la 2a y la 4a carta");
    }

    // El flop quema una carta y reparte tres
    @Test
    void repartirFlop() {
        crupier.repartirFlop(comunitarias);
        assertEquals(3, comunitarias.getCartas().size());
        assertEquals(48, crupier.getBaraja().getCartas().size(), "Se usan 4 cartas: 1 quemada y 3 del flop");
    }

    // El turn quema una carta y reparte una
    @Test
    void repartirTurn() {
        crupier.repartirTurn(comunitarias);
        assertEquals(1, comunitarias.getCartas().size());
        assertEquals(50, crupier.getBaraja().getCartas().size());
    }

    // El river quema una carta y reparte una
    @Test
    void repartirRiver() {
        crupier.repartirRiver(comunitarias);
        assertEquals(1, comunitarias.getCartas().size());
        assertEquals(50, crupier.getBaraja().getCartas().size());
    }
}
