package com.poker.servicio;

import com.poker.dobles.JugadorSimulado;
import com.poker.evaluador.EvaluadorManoEstandar;
import com.poker.modelo.*;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class ServicioShowdownTest {

    // Gana la mejor mano y si empatan se divide el bote
    @Test
    void repartirBotes() {
        CartasComunitarias comunitarias = new CartasComunitarias();
        comunitarias.agregar(new Carta(Valor.DOS, Palo.PICAS));
        comunitarias.agregar(new Carta(Valor.SIETE, Palo.CORAZONES));
        comunitarias.agregar(new Carta(Valor.NUEVE, Palo.TREBOLES));
        comunitarias.agregar(new Carta(Valor.JOTA, Palo.DIAMANTES));
        comunitarias.agregar(new Carta(Valor.CUATRO, Palo.PICAS));
        Jugador ana = new JugadorSimulado("Ana", 0);
        ana.recibirCarta(new Carta(Valor.AS, Palo.PICAS));
        ana.recibirCarta(new Carta(Valor.REY, Palo.PICAS));
        Jugador luis = new JugadorSimulado("Luis", 0);
        luis.recibirCarta(new Carta(Valor.JOTA, Palo.CORAZONES));
        luis.recibirCarta(new Carta(Valor.TRES, Palo.CORAZONES));
        Jugador sofia = new JugadorSimulado("Sofia", 0);
        sofia.recibirCarta(new Carta(Valor.JOTA, Palo.TREBOLES));
        sofia.recibirCarta(new Carta(Valor.TRES, Palo.DIAMANTES));
        ServicioShowdown servicio = new ServicioShowdown(new EvaluadorManoEstandar());

        // Luis gana con par de jotas
        Set<Jugador> anaYLuis = new LinkedHashSet<>(List.of(ana, luis));
        assertEquals(Map.of(luis, 200), servicio.repartirBotes(List.of(new Bote(200, anaYLuis)), comunitarias));

        // Luis y Sofia empatan y dividen el bote
        Set<Jugador> luisYSofia = new LinkedHashSet<>(List.of(luis, sofia));
        Map<Jugador, Integer> empate = servicio.repartirBotes(List.of(new Bote(101, luisYSofia)), comunitarias);
        assertEquals(51, empate.get(luis));
        assertEquals(50, empate.get(sofia));
    }
}
