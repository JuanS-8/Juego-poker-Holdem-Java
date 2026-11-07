package com.poker.controlador;

import com.poker.dto.JugadorDTO;
import com.poker.dto.MesaDTO;
import com.poker.servicio.ServicioJuego;
import com.poker.vista.VistaJuego;

public class ControladorJuego {
    private ServicioJuego servicioJuego;
    private VistaJuego vista;

    public ControladorJuego(ServicioJuego servicioJuego, VistaJuego vista) {
        this.servicioJuego = servicioJuego;
        this.vista = vista;
    }

    public void ejecutar() {
        // Continuar la última partida guardada o iniciar una nueva
        boolean cargada = false;
        if (vista.preguntarCargarPartida()) {
            servicioJuego.cargarPartida(null);
            cargada = !servicioJuego.obtenerMesa().asientos().isEmpty();
            if (!cargada) {
                vista.mostrarMensaje("No hay partidas guardadas. Se inicia una nueva.");
            }
        }
        if (!cargada) {
            servicioJuego.iniciarPartida(vista.pedirConfiguracion());
        }
        vista.mostrarMesa(servicioJuego.obtenerMesa());

        // Jugar manos hasta que termine la partida o el jugador quiera salir
        while (!servicioJuego.partidaTerminada()) {
            servicioJuego.jugarMano();
            if (servicioJuego.partidaTerminada())
                break;
            if (!vista.preguntarContinuar()) {
                // Al salir se puede guardar la partida
                if (vista.preguntarGuardar()) {
                    servicioJuego.guardarPartida();
                    vista.mostrarMensaje("Partida guardada.");
                }
                break;
            }
        }

        // Mostrar la mesa final y las estadísticas del jugador
        MesaDTO mesa = servicioJuego.obtenerMesa();
        vista.mostrarMensaje("============== FIN DE LA PARTIDA ==============");
        vista.mostrarMesa(mesa);
        if (mesa.asientos().isEmpty())
            return;
        JugadorDTO estadisticas = servicioJuego.consultarEstadisticas(mesa.asientos().get(0).nombre());
        if (estadisticas != null) {
            vista.mostrarMensaje("Estadísticas de " + estadisticas.nombre() + ": manos jugadas " + estadisticas.manosJugadas()
                    + ", manos ganadas " + estadisticas.manosGanadas() + ", máximo de fichas " + estadisticas.maximoFichas());
        }
    }
}
