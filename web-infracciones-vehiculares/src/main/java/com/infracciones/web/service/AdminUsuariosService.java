package com.infracciones.web.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class AdminUsuariosService {
    private final SoapClient soap;
    private final String url;

    public AdminUsuariosService(SoapClient soap, @Value("${soap.usuarios.url}") String url) {
        this.soap = soap;
        this.url = url;
    }

    public List<Map<String, String>> listar(String token) { return SoapRows.mapear(soap.invocar(url, "listarUsuarios", token)); }
    public List<Map<String, String>> consultar(String usuario, String token) { return SoapRows.mapear(soap.invocar(url, "consultarUsuarioPorUsername", token, "usuario", usuario)); }
    public List<Map<String, String>> crear(String dpi, String usuario, String contrasena, String rol, String estado, String token) {
        return SoapRows.mapear(soap.invocar(url, "crearUsuario", token, "dpiPersona", dpi, "usuario", usuario,
                "contrasena", contrasena, "nombreRol", rol, "estado", estado));
    }
    public List<Map<String, String>> actualizar(String usuario, String rol, String estado, String token) {
        return SoapRows.mapear(soap.invocar(url, "actualizarUsuario", token, "usuario", usuario, "nombreRol", rol, "estado", estado));
    }
    public void cambiarContrasena(String usuario, String nuevaContrasena, String token) {
        soap.invocar(url, "cambiarContrasena", token, "usuario", usuario, "nuevaContrasena", nuevaContrasena);
    }
    public void eliminar(String usuario, String token) { soap.invocar(url, "eliminarUsuario", token, "usuario", usuario); }
}
