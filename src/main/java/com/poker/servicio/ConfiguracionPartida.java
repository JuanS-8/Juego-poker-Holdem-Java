package com.poker.servicio;

import com.poker.bot.DificultadBot;

public record ConfiguracionPartida(String nombreJugador, int fichasIniciales, int ciegaPequena, int ciegaGrande,
        DificultadBot dificultad) {
}
