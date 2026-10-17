package com.infracciones.web.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.w3c.dom.Element;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class AgenteInfraccionService {

    private final SoapClient soap;
    private final String infraccionesUrl;
    private final String tiposUrl;
    private final String personasUrl;
    private final String vehiculosUrl;

    public AgenteInfraccionService(SoapClient soap,
                                   @Value("${soap.infracciones.url}") String infraccionesUrl,
                                   @Value("${soap.tipos.url}") String tiposUrl,
                                   @Value("${soap.personas.url}") String personasUrl,
                                   @Value("${soap.vehiculos.url}") String vehiculosUrl) {
        this.soap = soap;
        this.infraccionesUrl = infraccionesUrl;
        this.tiposUrl = tiposUrl;
        this.personasUrl = personasUrl;
        this.vehiculosUrl = vehiculosUrl;
    }

    public List<Map<String, String>> listarCatalogo(String token) {
        return soap.invocar(tiposUrl, "listarTipoInfracciones", token).stream()
                .map(e -> {
                    Map<String, String> tipo = new LinkedHashMap<>();
                    tipo.put("codigo", SoapClient.texto(e, "codigo"));
                    tipo.put("nombre", SoapClient.texto(e, "nombre"));
                    tipo.put("descripcion", SoapClient.texto(e, "descripcion"));
                    tipo.put("monto", SoapClient.texto(e, "monto"));
                    return tipo;
                }).toList();
    }

    public List<Map<String, String>> listarPersonas(String token) {
        return soap.invocar(personasUrl, "listarPersonas", token).stream().map(e -> {
            Map<String, String> persona = new LinkedHashMap<>();
            persona.put("dpi", SoapClient.texto(e, "dpiPersona"));
            persona.put("nombre", SoapClient.texto(e, "nombrePersona"));
            persona.put("apellido", SoapClient.texto(e, "apellidoPersona"));
            return persona;
        }).toList();
    }

    public List<Map<String, String>> listarVehiculos(String token) {
        return soap.invocar(vehiculosUrl, "listarVehiculos", token).stream().map(e -> {
            Map<String, String> vehiculo = new LinkedHashMap<>();
            vehiculo.put("placa", SoapClient.texto(e, "placa"));
            vehiculo.put("marca", SoapClient.texto(e, "marca"));
            vehiculo.put("modelo", SoapClient.texto(e, "modelo"));
            return vehiculo;
        }).toList();
    }

    public Map<String, String> registrar(String numero, String fecha, String direccion,
                                         String dpi, String placa, String usuarioAgente,
                                         String codigoTipo, String token) {
        List<Element> resultado = soap.invocar(infraccionesUrl, "registrarInfraccion", token,
                "numeroInfraccion", numero,
                "fechaInfraccion", fecha,
                "direccionInfraccion", direccion,
                "dpiPersona", dpi,
                "placaVehiculo", placa,
                "usuarioAgente", usuarioAgente,
                "codigoTipoInfraccion", codigoTipo);
        if (resultado.isEmpty()) {
            throw new ServicioInfraccionesException("El servicio no devolvió la boleta creada.");
        }

        Element e = resultado.get(0);
        Map<String, String> boleta = new LinkedHashMap<>();
        boleta.put("numero", SoapClient.texto(e, "numeroInfraccion"));
        boleta.put("placa", SoapClient.texto(e, "placaVehiculo"));
        boleta.put("tipoInfraccion", SoapClient.texto(e, "tipoInfraccion"));
        boleta.put("monto", SoapClient.texto(e, "montoOriginal"));
        boleta.put("fecha", SoapClient.texto(e, "fechaInfraccion"));
        boleta.put("direccion", SoapClient.texto(e, "direccionInfraccion"));
        boleta.put("dpi", SoapClient.texto(e, "dpiPersona"));
        boleta.put("nombrePersona", SoapClient.texto(e, "nombrePersona"));
        boleta.put("estado", SoapClient.texto(e, "estado"));
        boleta.put("agente", SoapClient.texto(e, "usuarioAgente"));
        return boleta;
    }
}
