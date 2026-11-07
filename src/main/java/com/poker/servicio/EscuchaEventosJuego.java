package com.poker.servicio;

import com.poker.dto.HistorialManoDTO;
import com.poker.dto.MesaDTO;
import com.poker.modelo.Accion;
import com.poker.modelo.FaseJuego;
import com.poker.modelo.Jugador;

public interface EscuchaEventosJuego {
    void alRealizarAccion(Jugador jugador, Accion accion);
    void alCambiarFase(FaseJuego fase, MesaDTO mesa);
    void alTerminarMano(HistorialManoDTO historial);
}
