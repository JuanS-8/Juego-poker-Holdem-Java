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

    // Color
    @Test
    void reglaColor() {
        assertEquals(CategoriaMano.COLOR, new ReglaColor().verificar(cartas("AC 9C 7C 4C 2C")).get().categoria());
    }

    // Escalera, incluida la del As bajo A-2-3-4-5
    @Test
    void reglaEscalera() {
        assertEquals(List.of(9), new ReglaEscalera().verificar(cartas("9C 8D 7T 6P 5C")).get().desempate());
        assertEquals(List.of(5), new ReglaEscalera().verificar(cartas("AC 2D 3T 4P 5C")).get().desempate());
    }

    // Trío
    @Test
    void reglaTrio() {
        assertEquals(CategoriaMano.TRIO, new ReglaTrio().verificar(cartas("7C 7D 7T KP 2C")).get().categoria());
    }

    // Doble par
    @Test
    void reglaDoblePar() {
        assertEquals(CategoriaMano.DOBLE_PAR, new ReglaDoblePar().verificar(cartas("7C 7D KT KP 2C")).get().categoria());
    }

    // Par, que no reconoce un full
    @Test
    void reglaPar() {
        assertEquals(CategoriaMano.PAR, new ReglaPar().verificar(cartas("7C 7D KT QP 2C")).get().categoria());
        assertTrue(new ReglaPar().verificar(cartas("KC KD KT 4P 4C")).isEmpty(), "Un full no es un par");
    }

    // Carta alta
    @Test
    void reglaCartaAlta() {
        assertEquals(CategoriaMano.CARTA_ALTA, new ReglaCartaAlta().verificar(cartas("AC 7D KT QP 2C")).get().categoria());
    }

    // Solo se aceptan 5 cartas
    @Test
    void sonCincoCartas() {
        assertTrue(ReglaMano.sonCincoCartas(cartas("AC 7D KT QP 2C")));
        assertFalse(ReglaMano.sonCincoCartas(cartas("AC 7D KT QP")));
        assertFalse(ReglaMano.sonCincoCartas(null));
    }

    // Cantidades de cada valor, de mayor a menor
    @Test
    void cantidades() {
        assertEquals(List.of(3, 2), ReglaMano.cantidades(cartas("KC KD KT 4P 4C")));
    }

    // Desempate: primero los grupos más grandes
    @Test
    void desempatePorGrupos() {
        assertEquals(List.of(13, 5, 9), ReglaMano.desempatePorGrupos(cartas("KC 5D KT 9P 5C")));
    }

    // Color cuando todas son del mismo palo
    @Test
    void esColor() {
        assertTrue(ReglaMano.esColor(cartas("AC 9C 7C 4C 2C")));
        assertFalse(ReglaMano.esColor(cartas("AC 9C 7C 4C 2D")));
    }

    // Carta alta de la escalera, o 0 si no es escalera
    @Test
    void cartaAltaEscalera() {
        assertEquals(9, ReglaMano.cartaAltaEscalera(cartas("9C 8D 7T 6P 5C")));
        assertEquals(0, ReglaMano.cartaAltaEscalera(cartas("9C 8D 7T 6P 4C")));
    }
}
