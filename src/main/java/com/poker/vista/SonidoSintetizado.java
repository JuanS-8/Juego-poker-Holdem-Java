package com.poker.vista;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class SonidoSintetizado implements ReproductorSonido {
    private float frecuenciaMuestreo;

    public SonidoSintetizado() {
        this.frecuenciaMuestreo = 44100;
    }

    @Override
    public void reproducir(TipoSonido sonido) {
        if (sonido == null)
            return;
        // Cada sonido son uno o dos tonos cortos
        byte[] primerTono;
        byte[] segundoTono = new byte[0];
        if (sonido == TipoSonido.BARAJAR) {
            primerTono = generarTono(220, 90);
            segundoTono = generarTono(260, 90);
        } else if (sonido == TipoSonido.REPARTIR) {
            primerTono = generarTono(660, 60);
        } else if (sonido == TipoSonido.FICHAS) {
            primerTono = generarTono(1200, 40);
            segundoTono = generarTono(1500, 40);
        } else {
            primerTono = generarTono(523, 150);
            segundoTono = generarTono(784, 250);
        }

        // Reproducir los tonos
        AudioFormat formato = new AudioFormat(frecuenciaMuestreo, 8, 1, true, false);
        try (SourceDataLine linea = AudioSystem.getSourceDataLine(formato)) {
            linea.open(formato);
            linea.start();
            linea.write(primerTono, 0, primerTono.length);
            linea.write(segundoTono, 0, segundoTono.length);
            linea.drain();
        } catch (LineUnavailableException | IllegalArgumentException | SecurityException excepcion) {
            // Si el equipo no tiene salida de sonido se sigue jugando sin sonido
        }
    }

    private byte[] generarTono(double frecuencia, int duracionMs) {
        //baja de volumen para que no suene un golpe al final
        int muestras = (int) (frecuenciaMuestreo * duracionMs / 1000);
        byte[] datos = new byte[muestras];
        for (int i = 0; i < muestras; i++) {
            double volumen = 60.0 * (muestras - i) / muestras;
            datos[i] = (byte) (Math.sin(2 * Math.PI * frecuencia * i / frecuenciaMuestreo) * volumen);
        }
        return datos;
    }
}
