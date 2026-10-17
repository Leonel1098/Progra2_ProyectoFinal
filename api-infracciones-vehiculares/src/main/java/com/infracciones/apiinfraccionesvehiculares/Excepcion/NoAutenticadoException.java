package com.infracciones.apiinfraccionesvehiculares.Excepcion;

public class NoAutenticadoException extends RuntimeException {
    public NoAutenticadoException(String mensaje) { super(mensaje); }
}