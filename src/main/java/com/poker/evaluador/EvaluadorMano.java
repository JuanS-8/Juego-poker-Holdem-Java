package com.poker.evaluador;

import com.poker.modelo.Carta;

import java.util.List;

public interface EvaluadorMano {
    ResultadoMano evaluar(List<Carta> propias, List<Carta> comunitarias);
}
