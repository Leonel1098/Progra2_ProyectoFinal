package com.infracciones.apiinfraccionesvehiculares.Excepcion;

public class PersonaYaTieneUsuarioException extends RuntimeException {
    public PersonaYaTieneUsuarioException(String message) {
        super(message);
    }
}