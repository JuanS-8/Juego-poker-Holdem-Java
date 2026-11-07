package com.poker.bot;

import com.poker.evaluador.EvaluadorManoEstandar;

import java.util.Random;

public class FabricaEstrategiaBot {

    public EstrategiaBot crear(DificultadBot dificultad) {
        // Fácil: juega al azar
        if (dificultad == DificultadBot.FACIL)
            return new EstrategiaAleatoria(new Random());
        // Difícil: agresivo
        if (dificultad == DificultadBot.DIFICIL)
            return new EstrategiaAgresiva(new EvaluadorManoEstandar());
        // Medio o sin dificultad: conservador
        return new EstrategiaConservadora(new EvaluadorManoEstandar());
    }
}
