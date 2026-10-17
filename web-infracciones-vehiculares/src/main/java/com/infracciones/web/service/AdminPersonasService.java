package com.infracciones.web.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class AdminPersonasService {
    private final SoapClient soap;
    private final String url;

    public AdminPersonasService(SoapClient soap, @Value("${soap.personas.url}") String url) {
        this.soap = soap;
        this.url = url;
    }

    public List<Map<String, String>> listar(String token) { return SoapRows.mapear(soap.invocar(url, "listarPersonas", token)); }
    public List<Map<String, String>> consultar(String dpi, String token) { return SoapRows.mapear(soap.invocar(url, "consultarPersonaPorDpi", token, "dpi", dpi)); }
    public List<Map<String, String>> crear(String dpi, String nombre, String apellido, String telefono, String direccion, String token) {
        return SoapRows.mapear(soap.invocar(url, "crearPersona", token, "dpi", dpi, "nombre", nombre, "apellido", apellido, "telefono", telefono, "direccion", direccion));
    }
    public List<Map<String, String>> actualizar(String dpi, String nombre, String apellido, String telefono, String direccion, String token) {
        return SoapRows.mapear(soap.invocar(url, "actualizarPersona", token, "dpi", dpi, "nombre", nombre, "apellido", apellido, "telefono", telefono, "direccion", direccion));
    }
    public void eliminar(String dpi, String token) { soap.invocar(url, "eliminarPersona", token, "dpi", dpi); }
}
