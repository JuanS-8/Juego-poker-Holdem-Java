package com.poker.bot;

import com.poker.evaluador.EvaluadorMano;
import com.poker.evaluador.ResultadoMano;
import com.poker.modelo.Accion;
import com.poker.modelo.ContextoDecision;
import com.poker.modelo.TipoAccion;

public class EstrategiaConservadora implements EstrategiaBot {
    private EvaluadorMano evaluador;

    public EstrategiaConservadora(EvaluadorMano evaluador) {
        this.evaluador = evaluador;
    }

    @Override
    public Accion decidir(ContextoDecision contexto) {
        // Sin acciones válidas se retira
        if (contexto == null || contexto.accionesValidas().isEmpty())
            return new Accion(TipoAccion.RETIRARSE, 0);

        // Calcular la fuerza de la mano de 0 a 10
        int fuerza = 2;
        if (contexto.cartasComunitarias().isEmpty() && contexto.cartasPropias().size() == 2) {
            // Antes del flop solo se miran las 2 cartas propias
            int primera = contexto.cartasPropias().get(0).valor().getNumero();
            int segunda = contexto.cartasPropias().get(1).valor().getNumero();
            int alta = Math.max(primera, segunda);
            int baja = Math.min(primera, segunda);
            if (alta == baja && alta >= 10) {
                fuerza = 8;
            } else if (alta == baja) {
                fuerza = 5;
            } else if (alta >= 13 && baja >= 10) {
                fuerza = 6;
            } else if (alta >= 12) {
                fuerza = 4;
            }
        } else {
            // Después del flop se evalúa la mano
            ResultadoMano resultado = evaluador.evaluar(contexto.cartasPropias(), contexto.cartasComunitarias());
            if (resultado != null) {
                switch (resultado.categoria()) {
                    case CARTA_ALTA: fuerza = 2; break;
                    case PAR: fuerza = 5; break;
                    case DOBLE_PAR: fuerza = 7; break;
                    default: fuerza = 9;
                }
            }
        }

        // Nadie ha apostado: apuesta solo con mano fuerte
        if (contexto.porIgualar() == 0) {
            if (fuerza >= 7 && contexto.accionesValidas().contains(TipoAccion.APOSTAR))
                return new Accion(TipoAccion.APOSTAR, contexto.subidaMinima());
            return new Accion(TipoAccion.PASAR, 0);
        }

        // Hay apuesta: iguala con mano fuerte o con mano media si la apuesta es baja
        boolean apuestaBaja = contexto.porIgualar() <= contexto.fichas() / 5;
        if (contexto.accionesValidas().contains(TipoAccion.IGUALAR) && (fuerza >= 7 || (fuerza >= 5 && apuestaBaja)))
            return new Accion(TipoAccion.IGUALAR, contexto.porIgualar());

        // No alcanza a igualar: va all-in solo con mano muy fuerte
        if (fuerza >= 8 && !contexto.accionesValidas().contains(TipoAccion.IGUALAR)
                && contexto.accionesValidas().contains(TipoAccion.ALL_IN))
            return new Accion(TipoAccion.ALL_IN, contexto.fichas());

        // En otro caso se retira
        return new Accion(TipoAccion.RETIRARSE, 0);
    }
}
