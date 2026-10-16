package com.infracciones.apiinfraccionesvehiculares.soap;

import com.infracciones.apiinfraccionesvehiculares.Model.InfraccionDetalle;
import com.infracciones.apiinfraccionesvehiculares.Model.InfraccionPublica;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;

import java.util.List;

@WebService(
        targetNamespace = "https://soap.apiinfraccionesvehiculares.infracciones.com/"
)
public interface InfraccionService {

    @WebMethod
    List<InfraccionDetalle> listarInfracciones();

    @WebMethod
    InfraccionDetalle consultarPorNumero(
            @WebParam(name = "numeroInfraccion") String numeroInfraccion
    );

    @WebMethod
    List<InfraccionDetalle> consultarPorPlaca(
            @WebParam(name = "placa") String placa
    );

    @WebMethod
    List<InfraccionDetalle> consultarPorDpi(
            @WebParam(name = "dpi") String dpi
    );

    @WebMethod
    InfraccionDetalle registrarInfraccion(
            @WebParam(name = "numeroInfraccion") String numeroInfraccion,
            @WebParam(name = "fechaInfraccion") String fechaInfraccion,
            @WebParam(name = "direccionInfraccion") String direccionInfraccion,
            @WebParam(name = "dpiPersona") String dpiPersona,
            @WebParam(name = "placaVehiculo") String placaVehiculo,
            @WebParam(name = "usuarioAgente") String usuarioAgente,
            @WebParam(name = "codigoTipoInfraccion") String codigoTipoInfraccion
    );

    @WebMethod
    List<InfraccionPublica> consultaPublicaPorPlaca(
            @WebParam(name = "placa") String placa
    );

    @WebMethod
    InfraccionPublica consultaPublicaPorNumero(
            @WebParam(name = "numeroInfraccion") String numeroInfraccion
    );
    @WebMethod
    InfraccionDetalle anularInfraccion(
            @WebParam(name = "numeroInfraccion") String numeroInfraccion
    );
}