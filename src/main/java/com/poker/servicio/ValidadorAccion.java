package com.poker.servicio;

import com.poker.excepcion.ExcepcionAccionInvalida;
import com.poker.modelo.Accion;
import com.poker.modelo.EstadoJugador;
import com.poker.modelo.Jugador;
import com.poker.modelo.TipoAccion;

import java.util.EnumSet;
import java.util.Set;

public class ValidadorAccion {

    public Set<TipoAccion> accionesValidas(Jugador jugador, int porIgualar, int subidaMinima) {
        Set<TipoAccion> validas = EnumSet.noneOf(TipoAccion.class);
        // Solo puede actuar un jugador activo y con fichas
        if (jugador == null || jugador.getEstado() != EstadoJugador.ACTIVO || jugador.getFichas() <= 0)
            return validas;
        // Siempre se puede retirar
        validas.add(TipoAccion.RETIRARSE);
        // Sin apuesta pendiente: pasar o apostar
        if (porIgualar <= 0) {
            validas.add(TipoAccion.PASAR);
            if (jugador.getFichas() > subidaMinima) {
                validas.add(TipoAccion.APOSTAR);
            }
        } else {
            // Con apuesta pendiente: igualar o subir si le alcanzan las fichas
            if (jugador.getFichas() > porIgualar) {
                validas.add(TipoAccion.IGUALAR);
            }
            if (jugador.getFichas() > porIgualar + subidaMinima) {
                validas.add(TipoAccion.SUBIR);
            }
        }
        // Siempre puede ir all-in
        validas.add(TipoAccion.ALL_IN);
        return validas;
    }

    public void validar(Accion accion, Jugador jugador, int porIgualar, int subidaMinima) {
        // Debe elegir una acción
        if (accion == null || accion.tipo() == null)
            throw new ExcepcionAccionInvalida("Debe elegir una acción");
        // La acción debe estar permitida
        if (!accionesValidas(jugador, porIgualar, subidaMinima).contains(accion.tipo()))
            throw new ExcepcionAccionInvalida("La acción " + accion.tipo() + " no está permitida en este momento");
        // La apuesta y la subida deben llegar al mínimo
        if (accion.tipo() == TipoAccion.APOSTAR && accion.monto() < subidaMinima)
            throw new ExcepcionAccionInvalida("La apuesta mínima es de " + subidaMinima + " fichas");
        if (accion.tipo() == TipoAccion.SUBIR && accion.monto() < porIgualar + subidaMinima)
            throw new ExcepcionAccionInvalida("La subida mínima es de " + (porIgualar + subidaMinima) + " fichas");
        // No puede apostar más de lo que tiene
        boolean apuesta = accion.tipo() == TipoAccion.APOSTAR || accion.tipo() == TipoAccion.SUBIR;
        if (apuesta && accion.monto() > jugador.getFichas())
            throw new ExcepcionAccionInvalida("No tiene fichas suficientes, puede ir all-in con " + jugador.getFichas() + " fichas");
    }
}
