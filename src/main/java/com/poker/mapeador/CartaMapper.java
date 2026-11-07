package com.poker.mapeador;

import com.poker.dto.CartaDTO;
import com.poker.modelo.Carta;
import com.poker.modelo.Palo;
import com.poker.modelo.Valor;

public class CartaMapper {

    public CartaDTO aDTO(Carta carta) {
        if (carta == null)
            return null;
        return new CartaDTO(carta.valor().name(), carta.palo().name());
    }

    public Carta aEntidad(CartaDTO dto) {
        if (dto == null)
            return null;
        return new Carta(Valor.valueOf(dto.valor()), Palo.valueOf(dto.palo()));
    }
}
