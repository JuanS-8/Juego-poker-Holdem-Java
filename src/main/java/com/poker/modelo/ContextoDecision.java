package com.poker.modelo;

import java.util.List;
import java.util.Set;

public record ContextoDecision(List<Carta> cartasPropias, List<Carta> cartasComunitarias, int porIgualar,
        int subidaMinima, int bote, int fichas, FaseJuego fase, Set<TipoAccion> accionesValidas) {
}
