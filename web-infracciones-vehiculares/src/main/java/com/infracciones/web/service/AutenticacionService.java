package com.infracciones.web.service;

import com.infracciones.web.dto.response.SesionUsuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.w3c.dom.Element;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
public class AutenticacionService {

    private static final String MENSAJE_CREDENCIALES = "Usuario o contraseña incorrectos.";

    private final SoapClient soap;
    private final String authUrl;

    public AutenticacionService(SoapClient soap, @Value("${soap.auth.url}") String authUrl) {
        this.soap = soap;
        this.authUrl = authUrl;
    }

    public SesionUsuario login(String usuario, String contrasena) {
        List<Element> resultado;
        try {
            resultado = soap.invocar(authUrl, "login", null,
                    "usuario", usuario, "contrasena", contrasena);
        } catch (SoapFaultException e) {
            if (MENSAJE_CREDENCIALES.equals(e.getMessage())) {
                throw new CredencialesInvalidasException(e.getMessage());
            }
            throw new ServicioInfraccionesException(
                    "El servicio de autenticación devolvió un error: " + e.getMessage(), e);
        }

        if (resultado.isEmpty()) {
            throw new ServicioInfraccionesException("Respuesta de login sin datos");
        }
        Element e = resultado.get(0);

        try {
            return new SesionUsuario(
                    SoapClient.texto(e, "usuario"),
                    SoapClient.texto(e, "nombreRol"),
                    SoapClient.texto(e, "nombrePersona"),
                    SoapClient.texto(e, "dpiPersona"),
                    SoapClient.texto(e, "token"),
                    Instant.parse(SoapClient.texto(e, "expiraEn")));
        } catch (DateTimeParseException ex) {
            throw new ServicioInfraccionesException("Respuesta de login inválida", ex);
        }
    }

    public void registrarCuenta(String dpiPersona, String nombre, String apellido,
                                String usuario, String contrasena) {
        try {
            soap.invocar(authUrl, "registrarCuenta", null,
                    "dpiPersona", dpiPersona,
                    "nombre", nombre,
                    "apellido", apellido,
                    "usuario", usuario,
                    "contrasena", contrasena);
        } catch (SoapFaultException e) {
            throw new ServicioInfraccionesException("No fue posible crear la cuenta", e);
        }
    }

    // Revoca el token en el backend; si falla no se bloquea el cierre de la sesión web.
    public void logout(String token) {
        try {
            soap.invocar(authUrl, "logout", token);
        } catch (RuntimeException ignorado) {
        }
    }
}
