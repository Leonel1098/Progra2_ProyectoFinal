package com.infracciones.apiinfraccionesvehiculares.Excepcion;

public class VehiculoEnUsoException extends RuntimeException {
    public VehiculoEnUsoException(String message) {
        super(message);
    }
}