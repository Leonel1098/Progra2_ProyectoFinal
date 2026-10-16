package com.infracciones.web.service;

public class SesionInvalidaException extends SoapFaultException {

    public SesionInvalidaException(String mensaje) {
        super(mensaje);
    }
}
