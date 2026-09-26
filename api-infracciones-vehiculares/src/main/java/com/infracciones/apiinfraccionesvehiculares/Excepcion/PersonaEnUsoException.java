package com.infracciones.apiinfraccionesvehiculares.Excepcion;

public class PersonaEnUsoException extends RuntimeException {
    public PersonaEnUsoException(String message) {
        super(message);
    }
}
