package com.poker.vista;

import com.poker.modelo.Accion;
import com.poker.modelo.ContextoDecision;

public interface ProveedorAccion {
    Accion pedirAccion(ContextoDecision contexto);
}
