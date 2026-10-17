package com.infracciones.apiinfraccionesvehiculares.Excepcion;

public class InfraccionDuplicadaException extends RuntimeException {
    public InfraccionDuplicadaException(String message) {
        super(message);
    }
}