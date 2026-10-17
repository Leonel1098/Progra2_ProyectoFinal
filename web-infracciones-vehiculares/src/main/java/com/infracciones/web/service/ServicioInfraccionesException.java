package com.infracciones.web.service;

public class ServicioInfraccionesException extends RuntimeException {

    public ServicioInfraccionesException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }

    public ServicioInfraccionesException(String mensaje) {
        super(mensaje);
    }
}