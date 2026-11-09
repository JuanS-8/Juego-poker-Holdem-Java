package com.poker.servicio;

import com.poker.dobles.JugadorSimulado;
import com.poker.modelo.Jugador;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class GestorBoteTest {
    private GestorBote gestor;
    private Jugador ana;
    private Jugador luis;
    private Jugador sofia;

    @BeforeEach
    void setUp() {
        gestor = new GestorBote();
        ana = new JugadorSimulado("Ana", 1000);
        luis = new JugadorSimulado("Luis", 1000);
        sofia = new JugadorSimulado("Sofia", 1000);
    }

    // Agregar aporte suma al jugador e ignora montos inválidos
    @Test
    void agregarAporte() {
        gestor.agregarAporte(ana, 10);
        gestor.agregarAporte(ana, 10);
        gestor.agregarAporte(ana, -5);
        gestor.agregarAporte(null, 10);
        assertEquals(20, gestor.total());
    }

    // Total suma los aportes de todos
    @Test
    void total() {
        gestor.agregarAporte(ana, 10);
        gestor.agregarAporte(luis, 20);
        assertEquals(30, gestor.total());
    }

    // All-in con bote secundario
    @Test
    void calcularBotes() {
        gestor.agregarAporte(ana, 100);
        gestor.agregarAporte(luis, 300);
        gestor.agregarAporte(sofia, 300);
        List<Bote> botes = gestor.calcularBotes();
        assertEquals(2, botes.size(), "Debe haber bote principal y secundario");
        assertEquals(300, botes.get(0).monto());
        assertEquals(Set.of(ana, luis, sofia), botes.get(0).elegibles());
        assertEquals(400, botes.get(1).monto());
        assertEquals(Set.of(luis, sofia), botes.get(1).elegibles());
    }

    // Reiniciar vacía el bote
    @Test
    void reiniciar() {
        gestor.agregarAporte(ana, 20);
        gestor.reiniciar();
        assertEquals(0, gestor.total());
    }
}
