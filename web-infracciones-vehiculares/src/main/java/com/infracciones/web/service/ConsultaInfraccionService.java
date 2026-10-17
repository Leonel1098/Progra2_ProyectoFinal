package com.infracciones.web.service;

import com.infracciones.web.dto.response.InfraccionPublica;
import com.infracciones.web.dto.response.InfraccionResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.w3c.dom.Element;

import java.util.List;

@Service
public class ConsultaInfraccionService {

    private final SoapClient soap;
    private final String url;

    public ConsultaInfraccionService(SoapClient soap, @Value("${soap.infracciones.url}") String url) {
        this.soap = soap;
        this.url = url;
    }

    // Operaciones públicas del backend: devuelven solo datos no personales, sin token.
    public List<InfraccionPublica> consultaPublicaPorPlaca(String placa) {
        return soap.invocar(url, "consultaPublicaPorPlaca", null, "placa", placa)
                .stream().map(this::aPublica).toList();
    }

    public List<InfraccionPublica> consultaPublicaPorNumero(String numeroInfraccion) {
        return soap.invocar(url, "consultaPublicaPorNumero", null,
                        "numeroInfraccion", numeroInfraccion)
                .stream().map(this::aPublica).toList();
    }

    public List<InfraccionPublica> consultarPorPlaca(String placa, String token) {
        return soap.invocar(url, "consultarPorPlaca", token, "placa", placa)
                .stream().map(this::aCompleta).map(InfraccionPublica::desde).toList();
    }

    public List<InfraccionPublica> consultarPorNumero(String numeroInfraccion, String token) {
        return soap.invocar(url, "consultarPorNumero", token,
                        "numeroInfraccion", numeroInfraccion)
                .stream().map(this::aCompleta).map(InfraccionPublica::desde).toList();
    }

    // Operaciones autenticadas: el backend aplica el rol y la propiedad por DPI.
    public List<InfraccionResponse> consultarPorDpi(String dpi, String token) {
        return soap.invocar(url, "consultarPorDpi", token, "dpi", dpi)
                .stream().map(this::aCompleta).toList();
    }

    private InfraccionPublica aPublica(Element e) {
        return new InfraccionPublica(
                SoapClient.texto(e, "numeroInfraccion"),
                SoapClient.texto(e, "fechaInfraccion"),
                SoapClient.texto(e, "tipoInfraccion"),
                SoapClient.texto(e, "montoOriginal"),
                SoapClient.texto(e, "estado"));
    }

    private InfraccionResponse aCompleta(Element e) {
        return new InfraccionResponse(
                SoapClient.texto(e, "idInfraccion"),
                SoapClient.texto(e, "numeroInfraccion"),
                SoapClient.texto(e, "fechaInfraccion"),
                SoapClient.texto(e, "direccionInfraccion"),
                SoapClient.texto(e, "tipoInfraccion"),
                SoapClient.texto(e, "montoOriginal"),
                SoapClient.texto(e, "estado"),
                SoapClient.texto(e, "placaVehiculo"),
                SoapClient.texto(e, "dpiPersona"),
                SoapClient.texto(e, "nombrePersona"),
                SoapClient.texto(e, "usuarioAgente"));
    }
}
