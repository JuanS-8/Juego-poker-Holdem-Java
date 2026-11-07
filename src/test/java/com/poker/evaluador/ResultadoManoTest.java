package com.poker.evaluador;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ResultadoManoTest {

    // Gana la categoría mayor y, si es la misma, el desempate
    @Test
    void compareTo() {
        ResultadoMano full = new ResultadoMano(CategoriaMano.FULL, List.of(), List.of(2, 3));
        ResultadoMano color = new ResultadoMano(CategoriaMano.COLOR, List.of(), List.of(14, 13));
        ResultadoMano parConNueve = new ResultadoMano(CategoriaMano.PAR, List.of(), List.of(10, 9));
        ResultadoMano parConOcho = new ResultadoMano(CategoriaMano.PAR, List.of(), List.of(10, 8));
        assertTrue(full.compareTo(color) > 0);
        assertTrue(parConNueve.compareTo(parConOcho) > 0, "El kicker desempata");
        assertEquals(0, parConNueve.compareTo(parConNueve));
    }
}
