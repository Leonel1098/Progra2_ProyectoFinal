package com.infracciones.apiinfraccionesvehiculares.Excepcion;


public class TipoInfraccionNoEncontradoException  extends RuntimeException{

    public TipoInfraccionNoEncontradoException(String message) {
        super(message);
    }
}
