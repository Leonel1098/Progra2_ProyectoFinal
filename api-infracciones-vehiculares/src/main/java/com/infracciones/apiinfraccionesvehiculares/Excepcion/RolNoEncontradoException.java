package com.infracciones.apiinfraccionesvehiculares.Excepcion;

public class RolNoEncontradoException extends RuntimeException {
    public RolNoEncontradoException(String message) {
        super(message);
    }
}