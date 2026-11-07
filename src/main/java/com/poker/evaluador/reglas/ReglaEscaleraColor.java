package com.poker.evaluador.reglas;

import com.poker.evaluador.CategoriaMano;
import com.poker.evaluador.ResultadoMano;
import com.poker.modelo.Carta;

import java.util.List;
import java.util.Optional;

public class ReglaEscaleraColor implements ReglaMano {

    @Override
    public Optional<ResultadoMano> verificar(List<Carta> cincoCartas) {
        // Validar que sean 5 cartas
        if (!ReglaMano.sonCincoCartas(cincoCartas))
            return Optional.empty();
        // Debe ser color y escalera que no termina en As
        int cartaAlta = ReglaMano.cartaAltaEscalera(cincoCartas);
        if (!ReglaMano.esColor(cincoCartas) || cartaAlta == 0 || cartaAlta == 14)
            return Optional.empty();
        // Crear el resultado con sus valores de desempate
        return Optional.of(new ResultadoMano(CategoriaMano.ESCALERA_COLOR, List.copyOf(cincoCartas), List.of(cartaAlta)));
    }
}
