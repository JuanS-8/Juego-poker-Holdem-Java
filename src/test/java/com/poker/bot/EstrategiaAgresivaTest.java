package com.poker.bot;

import com.poker.evaluador.EvaluadorManoEstandar;
import com.poker.modelo.*;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EstrategiaAgresivaTest {

    // Con par alto sube la apuesta mínima
    @Test
    void decidir() {
        EstrategiaBot estrategia = new EstrategiaAgresiva(new EvaluadorManoEstandar());
        List<Carta> parDeReyes = List.of(new Carta(Valor.REY, Palo.PICAS), new Carta(Valor.REY, Palo.CORAZONES));
        ContextoDecision contexto = new ContextoDecision(parDeReyes, List.of(), 20, 20, 30, 1000, FaseJuego.PRE_FLOP,
                EnumSet.of(TipoAccion.RETIRARSE, TipoAccion.IGUALAR, TipoAccion.SUBIR, TipoAccion.ALL_IN));
        assertEquals(new Accion(TipoAccion.SUBIR, 40), estrategia.decidir(contexto), "Iguala 20 y sube 20");
    }
}
