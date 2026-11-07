package com.poker.vista;

import com.poker.dto.HistorialManoDTO;
import com.poker.dto.MesaDTO;
import com.poker.servicio.ConfiguracionPartida;

public interface VistaJuego {
    ConfiguracionPartida pedirConfiguracion();
    void mostrarMesa(MesaDTO mesa);
    void mostrarMensaje(String mensaje);
    void mostrarResultado(HistorialManoDTO historial);
    boolean preguntarContinuar();
    boolean preguntarGuardar();
    boolean preguntarCargarPartida();
}
