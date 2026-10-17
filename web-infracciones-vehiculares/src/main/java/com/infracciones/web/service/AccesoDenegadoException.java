package com.infracciones.web.service;

public class AccesoDenegadoException extends SoapFaultException {

    public AccesoDenegadoException(String mensaje) {
        super(mensaje);
    }
}
