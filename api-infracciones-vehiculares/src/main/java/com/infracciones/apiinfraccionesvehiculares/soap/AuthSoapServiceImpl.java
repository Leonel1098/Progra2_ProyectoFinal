package com.infracciones.apiinfraccionesvehiculares.soap;

import com.infracciones.apiinfraccionesvehiculares.Model.LoginResultado;
import com.infracciones.apiinfraccionesvehiculares.Service.AuthService;
import com.infracciones.apiinfraccionesvehiculares.Model.Usuario;
import com.infracciones.apiinfraccionesvehiculares.Service.RegistroService;
import com.infracciones.apiinfraccionesvehiculares.Service.SeguridadService;
import jakarta.jws.WebService;
import org.springframework.stereotype.Service;

@Service
@WebService(
        serviceName = "AuthSoapService",
        endpointInterface = "com.infracciones.apiinfraccionesvehiculares.soap.AuthSoapService",
        targetNamespace = "https://soap.apiinfraccionesvehiculares.infracciones.com/"
)
public class AuthSoapServiceImpl implements AuthSoapService {

    private final AuthService authService;
    private final RegistroService registroService;
    private final SeguridadService seguridad;

    public AuthSoapServiceImpl(AuthService authService, RegistroService registroService,
                               SeguridadService seguridad) {
        this.authService = authService;
        this.registroService = registroService;
        this.seguridad = seguridad;
    }

    @Override
    public LoginResultado login(String usuario, String contrasena) {
        return authService.login(usuario, contrasena);
    }
    @Override
    public Usuario registrarCuenta(String dpiPersona, String nombre, String apellido,
                                   String usuario, String contrasena) {
        return registroService.registrarCuenta(dpiPersona, nombre, apellido, usuario, contrasena);
    }
    @Override
    public String logout() {
        seguridad.cerrarSesion();
        return "Sesión cerrada.";
    }
}