package com.poker.bot;

import com.poker.evaluador.EvaluadorMano;
import com.poker.evaluador.ResultadoMano;
import com.poker.modelo.Accion;
import com.poker.modelo.Carta;
import com.poker.modelo.ContextoDecision;
import com.poker.modelo.TipoAccion;

public class EstrategiaAgresiva implements EstrategiaBot {
    private EvaluadorMano evaluador;

    public EstrategiaAgresiva(EvaluadorMano evaluador) {
        this.evaluador = evaluador;
    }

    @Override
    public Accion decidir(ContextoDecision contexto) {
        // Sin acciones válidas se retira
        if (contexto == null || contexto.accionesValidas().isEmpty())
            return new Accion(TipoAccion.RETIRARSE, 0);

        // Calcular la fuerza de la mano de 0 a 10
        int fuerza = 3;
        if (contexto.cartasComunitarias().isEmpty() && contexto.cartasPropias().size() == 2) {
            // Antes del flop: pares, cartas altas o del mismo palo
            Carta primera = contexto.cartasPropias().get(0);
            Carta segunda = contexto.cartasPropias().get(1);
            int alta = Math.max(primera.valor().getNumero(), segunda.valor().getNumero());
            int baja = Math.min(primera.valor().getNumero(), segunda.valor().getNumero());
            if (alta == baja && alta >= 8) {
                fuerza = 8;
            } else if (alta == baja) {
                fuerza = 6;
            } else if (primera.palo() == segunda.palo() || alta >= 12) {
                fuerza = 5;
            } else if (alta >= 10) {
                fuerza = 4;
            }
        } else {
            // Después del flop se evalúa la mano
            ResultadoMano resultado = evaluador.evaluar(contexto.cartasPropias(), contexto.cartasComunitarias());
            if (resultado != null) {
                switch (resultado.categoria()) {
                    case CARTA_ALTA: fuerza = 3; break;
                    case PAR: fuerza = 6; break;
                    case DOBLE_PAR: fuerza = 8; break;
                    default: fuerza = 10;
                }
            }
        }

        // Nadie ha apostado: apuesta el doble de la mínima con casi cualquier mano
        if (contexto.porIgualar() == 0) {
            if (fuerza >= 4 && contexto.accionesValidas().contains(TipoAccion.APOSTAR)) {
                int monto = Math.min(contexto.subidaMinima() * 2, contexto.fichas());
                return new Accion(TipoAccion.APOSTAR, monto);
            }
            return new Accion(TipoAccion.PASAR, 0);
        }

        // Hay apuesta: sube con mano fuerte, iguala con mano media
        if (fuerza >= 7 && contexto.accionesValidas().contains(TipoAccion.SUBIR))
            return new Accion(TipoAccion.SUBIR, contexto.porIgualar() + contexto.subidaMinima());
        if (fuerza >= 4 && contexto.accionesValidas().contains(TipoAccion.IGUALAR))
            return new Accion(TipoAccion.IGUALAR, contexto.porIgualar());

        // No alcanza a igualar: va all-in con mano buena
        if (fuerza >= 6 && contexto.accionesValidas().contains(TipoAccion.ALL_IN))
            return new Accion(TipoAccion.ALL_IN, contexto.fichas());

        // En otro caso se retira
        return new Accion(TipoAccion.RETIRARSE, 0);
    }
}
