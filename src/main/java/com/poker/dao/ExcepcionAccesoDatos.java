package com.poker.dao;

public class ExcepcionAccesoDatos extends RuntimeException {

    public ExcepcionAccesoDatos(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
