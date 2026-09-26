package com.infracciones.apiinfraccionesvehiculares.Excepcion;

public class UsuarioEnUsoException extends RuntimeException {
    public UsuarioEnUsoException(String message) {
        super(message);
    }
}