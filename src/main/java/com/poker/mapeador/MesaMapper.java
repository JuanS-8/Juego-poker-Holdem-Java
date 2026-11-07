package com.poker.mapeador;

import com.poker.dto.AsientoDTO;
import com.poker.dto.CartaDTO;
import com.poker.dto.MesaDTO;
import com.poker.modelo.*;

import java.util.ArrayList;
import java.util.List;

public class MesaMapper {
    private CartaMapper cartaMapper;

    public MesaMapper() {
        this.cartaMapper = new CartaMapper();
    }

    public MesaDTO aDTO(Mesa mesa, FaseJuego fase, int bote, boolean mostrarBots) {
        if (mesa == null)
            return null;

        // Convertir cada jugador en un asiento
        List<AsientoDTO> asientos = new ArrayList<>();
        for (Jugador jugador : mesa.getJugadores()) {
            // Las cartas de los bots solo se ven en el showdown y si no se retiraron
            boolean visible = jugador instanceof JugadorHumano || (mostrarBots
                    && jugador.getEstado() != EstadoJugador.RETIRADO);
            List<CartaDTO> cartasVisibles = new ArrayList<>();
            if (visible) {
                for (Carta carta : jugador.getCartas()) {
                    cartasVisibles.add(cartaMapper.aDTO(carta));
                }
            }
            asientos.add(new AsientoDTO(jugador.getNombre(), jugador.getFichas(), jugador.getApuestaActual(),
                    jugador.getEstado().name(), cartasVisibles));
        }

        // Convertir las cartas comunitarias
        List<CartaDTO> comunitarias = new ArrayList<>();
        for (Carta carta : mesa.getComunitarias().getCartas()) {
            comunitarias.add(cartaMapper.aDTO(carta));
        }

        // Crear el DTO de la mesa
        String nombreFase = "";
        if (fase != null) {
            nombreFase = fase.name();
        }
        return new MesaDTO(nombreFase, bote, comunitarias, asientos);
    }
}
