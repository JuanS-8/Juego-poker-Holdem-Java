package com.poker.excepcion;

public class ExcepcionAccionInvalida extends RuntimeException {

    public ExcepcionAccionInvalida(String mensaje) {
        super(mensaje);
    }
}
