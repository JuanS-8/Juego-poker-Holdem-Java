package com.poker.mapeador;

import com.poker.dto.CartaDTO;
import com.poker.modelo.Carta;
import com.poker.modelo.Palo;
import com.poker.modelo.Valor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CartaMapperTest {
    private final CartaMapper mapper = new CartaMapper();

    // Carta a DTO
    @Test
    void aDTO() {
        assertEquals(new CartaDTO("REINA", "DIAMANTES"), mapper.aDTO(new Carta(Valor.REINA, Palo.DIAMANTES)));
        assertNull(mapper.aDTO(null));
    }

    // DTO a carta
    @Test
    void aEntidad() {
        assertEquals(new Carta(Valor.REINA, Palo.DIAMANTES), mapper.aEntidad(new CartaDTO("REINA", "DIAMANTES")));
        assertNull(mapper.aEntidad(null));
    }
}
