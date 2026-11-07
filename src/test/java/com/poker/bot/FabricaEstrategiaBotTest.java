package com.poker.bot;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FabricaEstrategiaBotTest {

    // Crea la estrategia según la dificultad
    @Test
    void crear() {
        FabricaEstrategiaBot fabrica = new FabricaEstrategiaBot();
        assertInstanceOf(EstrategiaAleatoria.class, fabrica.crear(DificultadBot.FACIL));
        assertInstanceOf(EstrategiaConservadora.class, fabrica.crear(DificultadBot.MEDIO));
        assertInstanceOf(EstrategiaAgresiva.class, fabrica.crear(DificultadBot.DIFICIL));
        assertInstanceOf(EstrategiaConservadora.class, fabrica.crear(null), "Sin dificultad usa medio");
    }
}
