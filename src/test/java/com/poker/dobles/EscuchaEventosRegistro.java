package com.poker.dobles;

import com.poker.dto.HistorialManoDTO;
import com.poker.dto.MesaDTO;
import com.poker.modelo.Accion;
import com.poker.modelo.FaseJuego;
import com.poker.modelo.Jugador;
import com.poker.servicio.EscuchaEventosJuego;

import java.util.ArrayList;
import java.util.List;

public class EscuchaEventosRegistro implements EscuchaEventosJuego {
    public List<Accion> acciones = new ArrayList<>();

    @Override
    public void alRealizarAccion(Jugador jugador, Accion accion) {
        acciones.add(accion);
    }

    @Override
    public void alCambiarFase(FaseJuego fase, MesaDTO mesa) {
    }

    @Override
    public void alTerminarMano(HistorialManoDTO historial) {
    }
}
