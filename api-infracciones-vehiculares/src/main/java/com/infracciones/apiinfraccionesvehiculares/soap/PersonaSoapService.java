package com.infracciones.apiinfraccionesvehiculares.soap;

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import com.infracciones.apiinfraccionesvehiculares.Model.Persona;

import java.util.List;

@WebService(
        targetNamespace = "https://soap.apiinfraccionesvehiculares.infracciones.com/"
)
public interface PersonaSoapService {

    @WebMethod
    List<Persona> listarPersonas();

    @WebMethod
    Persona consultarPersonaPorDpi(@WebParam(name = "dpi") String dpi);

    @WebMethod
    Persona crearPersona(
            @WebParam(name = "dpi") String dpi,
            @WebParam(name = "nombre") String nombre,
            @WebParam(name = "apellido") String apellido,
            @WebParam(name = "telefono") String telefono,
            @WebParam(name = "direccion") String direccion
    );

    @WebMethod
    Persona actualizarPersona(
            @WebParam(name = "dpi") String dpi,
            @WebParam(name = "nombre") String nombre,
            @WebParam(name = "apellido") String apellido,
            @WebParam(name = "telefono") String telefono,
            @WebParam(name = "direccion") String direccion
    );

    @WebMethod
    void eliminarPersona(@WebParam(name = "dpi") String dpi);
}