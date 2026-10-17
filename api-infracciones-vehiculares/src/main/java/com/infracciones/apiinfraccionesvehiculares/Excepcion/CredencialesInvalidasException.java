package com.infracciones.apiinfraccionesvehiculares.Excepcion;

public class CredencialesInvalidasException extends RuntimeException {
    public CredencialesInvalidasException(String message) {
        super(message);
    }
}