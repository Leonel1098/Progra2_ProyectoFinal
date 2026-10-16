package com.infracciones.apiinfraccionesvehiculares.soap;

import com.infracciones.apiinfraccionesvehiculares.Model.LoginResultado;
import com.infracciones.apiinfraccionesvehiculares.Model.Usuario;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;

@WebService(
        targetNamespace = "https://soap.apiinfraccionesvehiculares.infracciones.com/"
)
public interface AuthSoapService {

    @WebMethod
    LoginResultado login(
            @WebParam(name = "usuario") String usuario,
            @WebParam(name = "contrasena") String contrasena
    );
    @WebMethod
    Usuario registrarCuenta(
            @WebParam(name = "dpiPersona") String dpiPersona,
            @WebParam(name = "nombre") String nombre,
            @WebParam(name = "apellido") String apellido,
            @WebParam(name = "usuario") String usuario,
            @WebParam(name = "contrasena") String contrasena
    );

    @WebMethod
    String logout();
}