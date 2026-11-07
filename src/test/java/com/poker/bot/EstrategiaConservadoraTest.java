package com.poker.bot;

import com.poker.evaluador.EvaluadorManoEstandar;
import com.poker.modelo.*;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EstrategiaConservadoraTest {

    // Apuesta con par de ases y se retira con mano débil ante una apuesta alta
    @Test
    void decidir() {
        EstrategiaBot estrategia = new EstrategiaConservadora(new EvaluadorManoEstandar());
        List<Carta> parDeAses = List.of(new Carta(Valor.AS, Palo.PICAS), new Carta(Valor.AS, Palo.CORAZONES));
        List<Carta> manoDebil = List.of(new Carta(Valor.DOS, Palo.PICAS), new Carta(Valor.SIETE, Palo.CORAZONES));

        ContextoDecision sinApuesta = new ContextoDecision(parDeAses, List.of(), 0, 20, 30, 1000, FaseJuego.PRE_FLOP,
                EnumSet.of(TipoAccion.RETIRARSE, TipoAccion.PASAR, TipoAccion.APOSTAR, TipoAccion.ALL_IN));
        assertEquals(new Accion(TipoAccion.APOSTAR, 20), estrategia.decidir(sinApuesta));

        ContextoDecision apuestaAlta = new ContextoDecision(manoDebil, List.of(), 200, 20, 230, 1000, FaseJuego.PRE_FLOP,
                EnumSet.of(TipoAccion.RETIRARSE, TipoAccion.IGUALAR, TipoAccion.SUBIR, TipoAccion.ALL_IN));
        assertEquals(TipoAccion.RETIRARSE, estrategia.decidir(apuestaAlta).tipo());
    }
}
