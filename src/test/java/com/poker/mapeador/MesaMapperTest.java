package com.poker.mapeador;

import com.poker.dobles.BarajadorOrdenFijo;
import com.poker.dto.MesaDTO;
import com.poker.modelo.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MesaMapperTest {

    // Las cartas del bot están ocultas hasta el showdown
    @Test
    void aDTO() {
        Mesa mesa = new Mesa(new Crupier("Crupier", new Baraja(new BarajadorOrdenFijo())));
        mesa.agregarJugador(new JugadorHumano("h", "Juan", 1000, null));
        mesa.agregarJugador(new JugadorBot("b", "Bot Ana", 1000, null));
        mesa.getCrupier().prepararBaraja();
        mesa.getCrupier().repartirCartasPropias(mesa.getJugadores());
        MesaMapper mapper = new MesaMapper();

        MesaDTO duranteLaMano = mapper.aDTO(mesa, FaseJuego.FLOP, 60, false);
        assertEquals("FLOP", duranteLaMano.fase());
        assertEquals(2, duranteLaMano.asientos().get(0).cartasVisibles().size(), "El humano ve sus cartas");
        assertTrue(duranteLaMano.asientos().get(1).cartasVisibles().isEmpty(), "Las cartas del bot van ocultas");

        MesaDTO enShowdown = mapper.aDTO(mesa, FaseJuego.SHOWDOWN, 60, true);
        assertEquals(2, enShowdown.asientos().get(1).cartasVisibles().size());
    }
}
