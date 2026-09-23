package com.infracciones.apiinfraccionesvehiculares.Excepcion;

public class TipoInfraccionEnUsoException extends RuntimeException {

    public TipoInfraccionEnUsoException(String message) {
        super(message);
    }
}
