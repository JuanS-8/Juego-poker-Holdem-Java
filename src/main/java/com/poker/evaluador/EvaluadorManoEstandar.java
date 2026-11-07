package com.poker.evaluador;

import com.poker.evaluador.reglas.*;
import com.poker.modelo.Carta;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EvaluadorManoEstandar implements EvaluadorMano {
    private List<ReglaMano> reglas;

    public EvaluadorManoEstandar() {
        // Las reglas van de la categoría más alta a la más baja
        reglas = new ArrayList<>();
        reglas.add(new ReglaEscaleraReal());
        reglas.add(new ReglaEscaleraColor());
        reglas.add(new ReglaPoker());
        reglas.add(new ReglaFull());
        reglas.add(new ReglaColor());
        reglas.add(new ReglaEscalera());
        reglas.add(new ReglaTrio());
        reglas.add(new ReglaDoblePar());
        reglas.add(new ReglaPar());
        reglas.add(new ReglaCartaAlta());
    }

    @Override
    public ResultadoMano evaluar(List<Carta> propias, List<Carta> comunitarias) {
        // Validar los datos
        if (propias == null || comunitarias == null)
            return null;
        List<Carta> todas = new ArrayList<>(propias);
        todas.addAll(comunitarias);
        if (todas.size() < 5)
            return null;

        // Revisar cada combinación de 5 cartas con las reglas
        ResultadoMano mejor = null;
        for (List<Carta> cincoCartas : combinaciones(todas, 5)) {
            for (ReglaMano regla : reglas) {
                Optional<ResultadoMano> resultado = regla.verificar(cincoCartas);
                if (resultado.isPresent()) {
                    // Guardar la mejor mano encontrada
                    if (mejor == null || resultado.get().compareTo(mejor) > 0) {
                        mejor = resultado.get();
                    }
                    break;
                }
            }
        }
        return mejor;
    }

    private List<List<Carta>> combinaciones(List<Carta> cartas, int k) {
        List<List<Carta>> resultado = new ArrayList<>();
        // Recorrer todas las formas de tomar las cartas
        int total = 1 << cartas.size();
        for (int mascara = 0; mascara < total; mascara++) {
            // Solo sirven las combinaciones de k cartas
            if (Integer.bitCount(mascara) != k)
                continue;
            List<Carta> combinacion = new ArrayList<>();
            for (int i = 0; i < cartas.size(); i++) {
                if ((mascara & (1 << i)) != 0) {
                    combinacion.add(cartas.get(i));
                }
            }
            resultado.add(combinacion);
        }
        return resultado;
    }
}
