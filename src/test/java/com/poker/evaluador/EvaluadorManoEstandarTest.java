package com.poker.evaluador;

import com.poker.modelo.Carta;
import com.poker.modelo.Palo;
import com.poker.modelo.Valor;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EvaluadorManoEstandarTest {

    // Elige la mejor mano de 5 entre las 7 cartas
    @Test
    void evaluar() {
        EvaluadorMano evaluador = new EvaluadorManoEstandar();
        List<Carta> propias = List.of(new Carta(Valor.AS, Palo.CORAZONES), new Carta(Valor.AS, Palo.DIAMANTES));
        List<Carta> comunitarias = List.of(new Carta(Valor.AS, Palo.TREBOLES), new Carta(Valor.AS, Palo.PICAS),
                new Carta(Valor.REY, Palo.CORAZONES), new Carta(Valor.DOS, Palo.DIAMANTES), new Carta(Valor.TRES, Palo.TREBOLES));
        ResultadoMano resultado = evaluador.evaluar(propias, comunitarias);
        assertEquals(CategoriaMano.POKER, resultado.categoria());
        assertEquals(List.of(14, 13), resultado.desempate(), "Póker de ases con kicker rey");
        assertNull(evaluador.evaluar(propias, List.of()), "Con menos de 5 cartas no hay mano");
    }
}
