package com.infracciones.web.service;

import com.infracciones.web.dto.response.PagoResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.w3c.dom.Element;

import java.util.List;

@Service
public class PagoClienteService {

    private final SoapClient soap;
    private final String url;

    public PagoClienteService(SoapClient soap, @Value("${soap.pagos.url}") String url) {
        this.soap = soap;
        this.url = url;
    }

    public PagoResponse calcularMontoAPagar(String numeroInfraccion, String token) {
        return primero(soap.invocar(url, "calcularMontoAPagar", token,
                "numeroInfraccion", numeroInfraccion));
    }

    public PagoResponse aplicarPago(String numeroInfraccion, String token) {
        return primero(soap.invocar(url, "aplicarPago", token,
                "numeroInfraccion", numeroInfraccion));
    }

    private PagoResponse primero(List<Element> resultado) {
        if (resultado.isEmpty()) {
            throw new ServicioInfraccionesException("Respuesta de pago sin datos");
        }
        Element e = resultado.get(0);
        return new PagoResponse(
                SoapClient.texto(e, "idPago"),
                SoapClient.texto(e, "numeroInfraccion"),
                SoapClient.texto(e, "montoInicial"),
                SoapClient.texto(e, "porcentajeDescuento"),
                SoapClient.texto(e, "montoDescuento"),
                SoapClient.texto(e, "montoPagado"),
                SoapClient.texto(e, "fechaPago"));
    }
}
