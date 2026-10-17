package com.infracciones.apiinfraccionesvehiculares.Excepcion;

public class PersonaNoEncontradaException extends RuntimeException{
    public PersonaNoEncontradaException(String message) {
        super(message);
    }
}
