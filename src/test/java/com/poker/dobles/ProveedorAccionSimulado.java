package com.poker.dobles;

import com.poker.modelo.Accion;
import com.poker.modelo.ContextoDecision;
import com.poker.modelo.TipoAccion;
import com.poker.vista.ProveedorAccion;

public class ProveedorAccionSimulado implements ProveedorAccion {

    @Override
    public Accion pedirAccion(ContextoDecision contexto) {
        // Si no hay apuesta pasa
        if (contexto.porIgualar() == 0)
            return new Accion(TipoAccion.PASAR, 0);
        // Si le alcanzan las fichas iguala, si no va all-in
        if (contexto.accionesValidas().contains(TipoAccion.IGUALAR))
            return new Accion(TipoAccion.IGUALAR, contexto.porIgualar());
        return new Accion(TipoAccion.ALL_IN, contexto.fichas());
    }
}
