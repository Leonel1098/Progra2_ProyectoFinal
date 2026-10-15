package com.infracciones.apiinfraccionesvehiculares.soap;

import com.infracciones.apiinfraccionesvehiculares.Model.Pago;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;

import java.util.List;

@WebService(
        targetNamespace = "https://soap.apiinfraccionesvehiculares.infracciones.com/"
)
public interface PagoSoapService {

    @WebMethod
    Pago calcularMontoAPagar(@WebParam(name = "numeroInfraccion") String numeroInfraccion);

    @WebMethod
    Pago aplicarPago(@WebParam(name = "numeroInfraccion") String numeroInfraccion);

    @WebMethod
    Pago consultarPagoPorInfraccion(@WebParam(name = "numeroInfraccion") String numeroInfraccion);

    @WebMethod
    List<Pago> listarPagos();
}
