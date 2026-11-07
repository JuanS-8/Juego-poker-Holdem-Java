package com.poker.evaluador.reglas;

import com.poker.evaluador.CategoriaMano;
import com.poker.evaluador.ResultadoMano;
import com.poker.modelo.Carta;

import java.util.List;
import java.util.Optional;

public class ReglaCartaAlta implements ReglaMano {

    @Override
    public Optional<ResultadoMano> verificar(List<Carta> cincoCartas) {
        // Validar que sean 5 cartas
        if (!ReglaMano.sonCincoCartas(cincoCartas))
            return Optional.empty();
        // Deben ser 5 valores distintos, sin color ni escalera
        if (!ReglaMano.cantidades(cincoCartas).equals(List.of(1, 1, 1, 1, 1)) || ReglaMano.esColor(cincoCartas)
                || ReglaMano.cartaAltaEscalera(cincoCartas) != 0)
            return Optional.empty();
        // Crear el resultado con sus valores de desempate
        return Optional.of(new ResultadoMano(CategoriaMano.CARTA_ALTA, List.copyOf(cincoCartas), ReglaMano.desempatePorGrupos(cincoCartas)));
    }
}
