package com.poker.bot;

import com.poker.modelo.Accion;
import com.poker.modelo.ContextoDecision;
import com.poker.modelo.TipoAccion;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EstrategiaAleatoria implements EstrategiaBot {
    private Random aleatorio;

    public EstrategiaAleatoria(Random aleatorio) {
        this.aleatorio = aleatorio;
    }

    @Override
    public Accion decidir(ContextoDecision contexto) {
        // Sin acciones válidas se retira
        if (contexto == null || contexto.accionesValidas().isEmpty())
            return new Accion(TipoAccion.RETIRARSE, 0);

        // Solo va all-in si no tiene otra opción para seguir en la mano
        List<TipoAccion> opciones = new ArrayList<>(contexto.accionesValidas());
        if (opciones.size() > 2) {
            opciones.remove(TipoAccion.ALL_IN);
        }

        // Elegir una acción al azar y ponerle el monto mínimo
        TipoAccion elegida = opciones.get(aleatorio.nextInt(opciones.size()));
        if (elegida == TipoAccion.IGUALAR)
            return new Accion(elegida, contexto.porIgualar());
        if (elegida == TipoAccion.APOSTAR)
            return new Accion(elegida, contexto.subidaMinima());
        if (elegida == TipoAccion.SUBIR)
            return new Accion(elegida, contexto.porIgualar() + contexto.subidaMinima());
        if (elegida == TipoAccion.ALL_IN)
            return new Accion(elegida, contexto.fichas());
        return new Accion(elegida, 0);
    }
}
