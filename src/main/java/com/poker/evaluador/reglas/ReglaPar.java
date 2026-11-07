package com.poker.evaluador.reglas;

import com.poker.evaluador.CategoriaMano;
import com.poker.evaluador.ResultadoMano;
import com.poker.modelo.Carta;

import java.util.List;
import java.util.Optional;

public class ReglaPar implements ReglaMano {

    @Override
    public Optional<ResultadoMano> verificar(List<Carta> cincoCartas) {
        // Validar que sean 5 cartas
        if (!ReglaMano.sonCincoCartas(cincoCartas))
            return Optional.empty();
        // Debe haber un par y 3 cartas sueltas
        if (!ReglaMano.cantidades(cincoCartas).equals(List.of(2, 1, 1, 1)))
            return Optional.empty();
        // Crear el resultado con sus valores de desempate
        return Optional.of(new ResultadoMano(CategoriaMano.PAR, List.copyOf(cincoCartas), ReglaMano.desempatePorGrupos(cincoCartas)));
    }
}
