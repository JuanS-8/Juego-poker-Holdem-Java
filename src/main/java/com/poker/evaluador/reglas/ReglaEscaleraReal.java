package com.poker.evaluador.reglas;

import com.poker.evaluador.CategoriaMano;
import com.poker.evaluador.ResultadoMano;
import com.poker.modelo.Carta;

import java.util.List;
import java.util.Optional;

public class ReglaEscaleraReal implements ReglaMano {

    @Override
    public Optional<ResultadoMano> verificar(List<Carta> cincoCartas) {
        // Validar que sean 5 cartas
        if (!ReglaMano.sonCincoCartas(cincoCartas))
            return Optional.empty();
        // Debe ser color y escalera que termina en As
        if (!ReglaMano.esColor(cincoCartas) || ReglaMano.cartaAltaEscalera(cincoCartas) != 14)
            return Optional.empty();
        // Crear el resultado con sus valores de desempate
        return Optional.of(new ResultadoMano(CategoriaMano.ESCALERA_REAL, List.copyOf(cincoCartas), List.of(14)));
    }
}
