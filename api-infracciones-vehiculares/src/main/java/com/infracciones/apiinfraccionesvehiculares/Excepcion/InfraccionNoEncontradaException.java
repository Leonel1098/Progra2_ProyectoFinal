package com.infracciones.apiinfraccionesvehiculares.Excepcion;

public class InfraccionNoEncontradaException extends RuntimeException {
    public InfraccionNoEncontradaException(String message) {
        super(message);
    }
}