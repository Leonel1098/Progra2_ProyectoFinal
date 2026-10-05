package com.infracciones.apiinfraccionesvehiculares.soap;

import com.infracciones.apiinfraccionesvehiculares.Model.Vehiculo;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;

import java.util.List;

@WebService(
        targetNamespace = "https://soap.apiinfraccionesvehiculares.infracciones.com/"
)
public interface VehiculoSoapService {

    @WebMethod
    List<Vehiculo> listarVehiculos();

    @WebMethod
    Vehiculo consultarVehiculoPorPlaca(
            @WebParam(name = "placa") String placa
    );

    @WebMethod
    Vehiculo crearVehiculo(
            @WebParam(name = "placa") String placa,
            @WebParam(name = "marca") String marca,
            @WebParam(name = "modelo") String modelo,
            @WebParam(name = "color") String color
    );

    @WebMethod
    Vehiculo actualizarVehiculo(
            @WebParam(name = "placa") String placa,
            @WebParam(name = "marca") String marca,
            @WebParam(name = "modelo") String modelo,
            @WebParam(name = "color") String color
    );

    @WebMethod
    void eliminarVehiculo(
            @WebParam(name = "placa") String placa
    );
}