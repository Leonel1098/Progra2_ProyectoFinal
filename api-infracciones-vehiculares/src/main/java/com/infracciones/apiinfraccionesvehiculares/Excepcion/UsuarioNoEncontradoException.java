package com.infracciones.apiinfraccionesvehiculares.Excepcion;

public class UsuarioNoEncontradoException extends RuntimeException {
    public UsuarioNoEncontradoException(String message) {
        super(message);
    }
}