package com.poker.evaluador.reglas;

import com.poker.evaluador.CategoriaMano;
import com.poker.evaluador.ResultadoMano;
import com.poker.modelo.Carta;

import java.util.List;
import java.util.Optional;

public class ReglaEscalera implements ReglaMano {

    @Override
    public Optional<ResultadoMano> verificar(List<Carta> cincoCartas) {
        // Validar que sean 5 cartas
        if (!ReglaMano.sonCincoCartas(cincoCartas))
            return Optional.empty();
        // Debe ser escalera y no ser color
        int cartaAlta = ReglaMano.cartaAltaEscalera(cincoCartas);
        if (cartaAlta == 0 || ReglaMano.esColor(cincoCartas))
            return Optional.empty();
        // Crear el resultado con sus valores de desempate
        return Optional.of(new ResultadoMano(CategoriaMano.ESCALERA, List.copyOf(cincoCartas), List.of(cartaAlta)));
    }
}
