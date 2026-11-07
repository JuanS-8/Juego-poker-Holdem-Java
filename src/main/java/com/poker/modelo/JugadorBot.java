package com.poker.modelo;

import com.poker.bot.EstrategiaBot;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JugadorBot extends Jugador {
    private EstrategiaBot estrategia;

    public JugadorBot(String id, String nombre, int fichas, EstrategiaBot estrategia) {
        super(id, nombre, fichas);
        this.estrategia = estrategia;
    }

    @Override
    public Accion decidirAccion(ContextoDecision contexto) {
        return estrategia.decidir(contexto);
    }
}
