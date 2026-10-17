package com.infracciones.apiinfraccionesvehiculares.Excepcion;

public class UsuarioDuplicadoException extends RuntimeException {
    public UsuarioDuplicadoException(String message) {
        super(message);
    }
}