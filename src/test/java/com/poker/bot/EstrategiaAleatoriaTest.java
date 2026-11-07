package com.poker.bot;

import com.poker.modelo.Accion;
import com.poker.modelo.ContextoDecision;
import com.poker.modelo.FaseJuego;
import com.poker.modelo.TipoAccion;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class EstrategiaAleatoriaTest {

    // Siempre elige una acción válida y con el monto correcto
    @Test
    void decidir() {
        EstrategiaBot estrategia = new EstrategiaAleatoria(new Random(1));
        Set<TipoAccion> validas = EnumSet.of(TipoAccion.RETIRARSE, TipoAccion.IGUALAR, TipoAccion.SUBIR, TipoAccion.ALL_IN);
        ContextoDecision contexto = new ContextoDecision(List.of(), List.of(), 30, 20, 60, 500, FaseJuego.FLOP, validas);
        for (int i = 0; i < 100; i++) {
            Accion accion = estrategia.decidir(contexto);
            assertTrue(validas.contains(accion.tipo()), "La acción debe ser válida");
            if (accion.tipo() == TipoAccion.IGUALAR)
                assertEquals(30, accion.monto());
            if (accion.tipo() == TipoAccion.SUBIR)
                assertEquals(50, accion.monto());
        }
    }
}
