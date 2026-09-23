package com.infracciones.apiinfraccionesvehiculares.soap;

import jakarta.jws.WebService;
import org.springframework.stereotype.Service;

@Service
@WebService(
        serviceName = "InfraccionService",
        endpointInterface = "com.infracciones.apiinfraccionesvehiculares.soap.InfraccionService",
        targetNamespace = "https://soap.apiinfraccionesvehiculares.infracciones.com/"
)
public class InfraccionServiceImpl implements InfraccionService {

    @Override
    public String consultarEstado(String numeroInfraccion) {
        return "Servicio SOAP funcionando para la infracción: " + numeroInfraccion;
    }
}