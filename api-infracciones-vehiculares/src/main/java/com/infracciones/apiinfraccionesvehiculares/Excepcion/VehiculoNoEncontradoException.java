package com.infracciones.apiinfraccionesvehiculares.Excepcion;

public class VehiculoNoEncontradoException extends RuntimeException {
    public VehiculoNoEncontradoException(String message) {
        super(message);
    }
}