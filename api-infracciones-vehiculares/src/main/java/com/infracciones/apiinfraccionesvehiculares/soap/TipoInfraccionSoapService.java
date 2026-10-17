package com.infracciones.apiinfraccionesvehiculares.soap;

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import com.infracciones.apiinfraccionesvehiculares.Model.TipoInfraccion;

import java.math.BigDecimal;
import java.util.List;

@WebService(
        targetNamespace = "https://soap.apiinfraccionesvehiculares.infracciones.com/"
)
public interface TipoInfraccionSoapService {

    @WebMethod
    List<TipoInfraccion> listarTipoInfracciones();

    @WebMethod
    TipoInfraccion consultarTipoInfraccionPorId(@WebParam(name = "id") Integer id);

    @WebMethod
    TipoInfraccion consultarTipoInfraccionPorCodigo(@WebParam(name = "codigo") String codigo);

    @WebMethod
    TipoInfraccion crearTipoInfraccion(
            @WebParam(name = "codigo") String codigo,
            @WebParam(name = "nombre") String nombre,
            @WebParam(name = "descripcion") String descripcion,
            @WebParam(name = "monto") BigDecimal monto
    );

    @WebMethod
    TipoInfraccion actualizarTipoInfraccion(
            @WebParam(name = "id") Integer id,
            @WebParam(name = "codigo") String codigo,
            @WebParam(name = "nombre") String nombre,
            @WebParam(name = "descripcion") String descripcion,
            @WebParam(name = "monto") BigDecimal monto
    );

    @WebMethod
    void eliminarTipoInfraccion(@WebParam(name = "id") Integer id);
}