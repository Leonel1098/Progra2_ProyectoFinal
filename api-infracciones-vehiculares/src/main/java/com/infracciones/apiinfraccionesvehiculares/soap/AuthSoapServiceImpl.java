package com.infracciones.apiinfraccionesvehiculares.soap;

import com.infracciones.apiinfraccionesvehiculares.Model.LoginResultado;
import com.infracciones.apiinfraccionesvehiculares.Service.AuthService;
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

    public AuthSoapServiceImpl(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public LoginResultado login(String usuario, String contrasena) {
        return authService.login(usuario, contrasena);
    }
}