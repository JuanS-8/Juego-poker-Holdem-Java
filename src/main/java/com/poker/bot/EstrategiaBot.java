package com.poker.bot;

import com.poker.modelo.Accion;
import com.poker.modelo.ContextoDecision;

public interface EstrategiaBot {
    Accion decidir(ContextoDecision contexto);
}
