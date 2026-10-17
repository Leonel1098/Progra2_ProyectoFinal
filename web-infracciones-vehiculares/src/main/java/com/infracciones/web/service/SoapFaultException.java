package com.infracciones.web.service;

public class SoapFaultException extends RuntimeException {

    public SoapFaultException(String mensaje) {
        super(mensaje);
    }
}
