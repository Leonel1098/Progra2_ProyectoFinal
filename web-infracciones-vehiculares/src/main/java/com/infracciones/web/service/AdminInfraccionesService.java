package com.infracciones.web.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class AdminInfraccionesService {
    private final SoapClient soap;
    private final String url;

    public AdminInfraccionesService(SoapClient soap, @Value("${soap.infracciones.url}") String url) {
        this.soap = soap;
        this.url = url;
    }

    public List<Map<String, String>> listar(String token) {
        return SoapRows.mapear(soap.invocar(url, "listarInfracciones", token));
    }

    public List<Map<String, String>> porNumero(String numero, String token) {
        return SoapRows.mapear(soap.invocar(url, "consultarPorNumero", token, "numeroInfraccion", numero));
    }

    public List<Map<String, String>> porPlaca(String placa, String token) {
        return SoapRows.mapear(soap.invocar(url, "consultarPorPlaca", token, "placa", placa));
    }

    public List<Map<String, String>> porDpi(String dpi, String token) {
        return SoapRows.mapear(soap.invocar(url, "consultarPorDpi", token, "dpi", dpi));
    }

    public void anular(String numero, String token) {
        soap.invocar(url, "anularInfraccion", token, "numeroInfraccion", numero);
    }
}
