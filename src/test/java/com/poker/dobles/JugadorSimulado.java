package com.poker.dobles;

import com.poker.modelo.Accion;
import com.poker.modelo.ContextoDecision;
import com.poker.modelo.Jugador;
import com.poker.modelo.TipoAccion;

import java.util.ArrayList;
import java.util.List;

public class JugadorSimulado extends Jugador {
    private List<Accion> acciones = new ArrayList<>();

    public JugadorSimulado(String nombre, int fichas, Accion... acciones) {
        super(nombre, nombre, fichas);
        for (Accion accion : acciones) {
            this.acciones.add(accion);
        }
    }

    @Override
    public Accion decidirAccion(ContextoDecision contexto) {
        // Usar las acciones indicadas en orden
        if (!acciones.isEmpty())
            return acciones.remove(0);
        // Si no hay más, pasa o iguala
        if (contexto.porIgualar() == 0)
            return new Accion(TipoAccion.PASAR, 0);
        if (contexto.accionesValidas().contains(TipoAccion.IGUALAR))
            return new Accion(TipoAccion.IGUALAR, contexto.porIgualar());
        return new Accion(TipoAccion.ALL_IN, contexto.fichas());
    }
}
