package com.infracciones.apiinfraccionesvehiculares.soap;

import jakarta.jws.WebMethod;
import jakarta.jws.WebService;

@WebService(


        targetNamespace = "https://soap.apiinfraccionesvehiculares.infracciones.com/"
)
public interface InfraccionService {

    @WebMethod
    String consultarEstado(String numeroInfraccion);
}