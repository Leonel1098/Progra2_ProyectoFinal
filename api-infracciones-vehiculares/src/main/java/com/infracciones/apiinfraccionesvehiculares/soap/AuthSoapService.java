package com.infracciones.apiinfraccionesvehiculares.soap;

import com.infracciones.apiinfraccionesvehiculares.Model.LoginResultado;
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
}