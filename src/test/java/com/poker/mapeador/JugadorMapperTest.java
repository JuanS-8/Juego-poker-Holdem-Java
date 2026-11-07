package com.poker.mapeador;

import com.poker.dto.JugadorDTO;
import com.poker.modelo.Jugador;
import com.poker.modelo.JugadorBot;
import com.poker.modelo.JugadorHumano;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class JugadorMapperTest {
    private final JugadorMapper mapper = new JugadorMapper();

    // Jugador a DTO
    @Test
    void aDTO() {
        JugadorDTO dto = mapper.aDTO(new JugadorBot("b1", "Bot Ana", 800, null));
        assertEquals("Bot Ana", dto.nombre());
        assertEquals(800, dto.fichas());
        assertTrue(dto.esBot());
        assertNull(mapper.aDTO(null));
    }

    // DTO a jugador humano o bot
    @Test
    void aEntidad() {
        assertInstanceOf(JugadorBot.class, mapper.aEntidad(new JugadorDTO("b1", "Bot", 500, true, 0, 0, 0)));
        Jugador humano = mapper.aEntidad(new JugadorDTO("h1", "Juan", 700, false, 0, 0, 0));
        assertInstanceOf(JugadorHumano.class, humano);
        assertEquals(700, humano.getFichas());
    }
}
