package com.infracciones.apiinfraccionesvehiculares.Excepcion;

public class PagoNoEncontradoException extends RuntimeException {
    public PagoNoEncontradoException(String message) {
        super(message);
    }
}
