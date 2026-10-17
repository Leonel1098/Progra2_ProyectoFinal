package com.infracciones.web.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class AdminVehiculosService {
    private final SoapClient soap;
    private final String url;

    public AdminVehiculosService(SoapClient soap, @Value("${soap.vehiculos.url}") String url) {
        this.soap = soap;
        this.url = url;
    }

    public List<Map<String, String>> listar(String token) { return SoapRows.mapear(soap.invocar(url, "listarVehiculos", token)); }
    public List<Map<String, String>> consultar(String placa, String token) { return SoapRows.mapear(soap.invocar(url, "consultarVehiculoPorPlaca", token, "placa", placa)); }
    public List<Map<String, String>> crear(String placa, String marca, String modelo, String color, String token) {
        return SoapRows.mapear(soap.invocar(url, "crearVehiculo", token, "placa", placa, "marca", marca, "modelo", modelo, "color", color));
    }
    public List<Map<String, String>> actualizar(String placa, String marca, String modelo, String color, String token) {
        return SoapRows.mapear(soap.invocar(url, "actualizarVehiculo", token, "placa", placa, "marca", marca, "modelo", modelo, "color", color));
    }
    public void eliminar(String placa, String token) { soap.invocar(url, "eliminarVehiculo", token, "placa", placa); }
}
