package com.poker.evaluador.reglas;

import com.poker.evaluador.CategoriaMano;
import com.poker.modelo.Carta;
import com.poker.modelo.Palo;
import com.poker.modelo.Valor;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ReglaManoTest {

    // Metodo para no repetir las cartas
    private List<Carta> cartas(String texto) {
        List<Carta> resultado = new ArrayList<>();
        for (String parte : texto.split(" ")) {
            Valor valor;
            switch (parte.charAt(0)) {
                case 'A': valor = Valor.AS; break;
                case 'K': valor = Valor.REY; break;
                case 'Q': valor = Valor.REINA; break;
                case 'J': valor = Valor.JOTA; break;
                case 'T': valor = Valor.DIEZ; break;
                default: valor = Valor.values()[parte.charAt(0) - '2'];
            }
            Palo palo;
            switch (parte.charAt(1)) {
                case 'C': palo = Palo.CORAZONES; break;
                case 'D': palo = Palo.DIAMANTES; break;
                case 'T': palo = Palo.TREBOLES; break;
                default: palo = Palo.PICAS;
            }
            resultado.add(new Carta(valor, palo));
        }
        return resultado;
    }

    // Escalera real
    @Test
    void reglaEscaleraReal() {
        assertEquals(CategoriaMano.ESCALERA_REAL, new ReglaEscaleraReal().verificar(cartas("AP KP QP JP TP")).get().categoria());
    }

    // Escalera de color
    @Test
    void reglaEscaleraColor() {
        assertEquals(CategoriaMano.ESCALERA_COLOR, new ReglaEscaleraColor().verificar(cartas("9C 8C 7C 6C 5C")).get().categoria());
    }

    // Póker
    @Test
    void reglaPoker() {
        assertEquals(CategoriaMano.POKER, new ReglaPoker().verificar(cartas("9C 9D 9T 9P 2C")).get().categoria());
    }

    // Full, con su desempate: primero el trío y luego el par
    @Test
    void reglaFull() {
        assertEquals(List.of(13, 4), new ReglaFull().verificar(cartas("KC KD KT 4P 4C")).get().desempate());
    }
}
