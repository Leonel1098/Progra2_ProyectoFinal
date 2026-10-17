package com.infracciones.web.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class AdminTiposInfraccionService {
    private final SoapClient soap;
    private final String url;

    public AdminTiposInfraccionService(SoapClient soap, @Value("${soap.tipos.url}") String url) {
        this.soap = soap;
        this.url = url;
    }

    public List<Map<String, String>> listar(String token) { return SoapRows.mapear(soap.invocar(url, "listarTipoInfracciones", token)); }
    public List<Map<String, String>> porCodigo(String codigo, String token) { return SoapRows.mapear(soap.invocar(url, "consultarTipoInfraccionPorCodigo", token, "codigo", codigo)); }
    public List<Map<String, String>> crear(String codigo, String nombre, String descripcion, String monto, String token) {
        return SoapRows.mapear(soap.invocar(url, "crearTipoInfraccion", token, "codigo", codigo, "nombre", nombre, "descripcion", descripcion, "monto", monto));
    }
    public List<Map<String, String>> actualizar(String id, String codigo, String nombre, String descripcion, String monto, String token) {
        return SoapRows.mapear(soap.invocar(url, "actualizarTipoInfraccion", token, "id", id, "codigo", codigo, "nombre", nombre, "descripcion", descripcion, "monto", monto));
    }
    public void eliminar(String id, String token) { soap.invocar(url, "eliminarTipoInfraccion", token, "id", id); }
}
