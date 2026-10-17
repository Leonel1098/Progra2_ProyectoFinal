package com.infracciones.apiinfraccionesvehiculares.soap;

import com.infracciones.apiinfraccionesvehiculares.Model.Usuario;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;

import java.util.List;

@WebService(
        targetNamespace = "https://soap.apiinfraccionesvehiculares.infracciones.com/"
)
public interface UsuarioSoapService {

    @WebMethod
    List<Usuario> listarUsuarios();

    @WebMethod
    Usuario consultarUsuarioPorUsername(@WebParam(name = "usuario") String usuario);

    @WebMethod
    Usuario crearUsuario(
            @WebParam(name = "dpiPersona") String dpiPersona,
            @WebParam(name = "usuario") String usuario,
            @WebParam(name = "contrasena") String contrasena,
            @WebParam(name = "nombreRol") String nombreRol,
            @WebParam(name = "estado") Boolean estado
    );

    @WebMethod
    Usuario actualizarUsuario(
            @WebParam(name = "usuario") String usuario,
            @WebParam(name = "nombreRol") String nombreRol,
            @WebParam(name = "estado") Boolean estado
    );

    @WebMethod
    void cambiarContrasena(
            @WebParam(name = "usuario") String usuario,
            @WebParam(name = "nuevaContrasena") String nuevaContrasena
    );

    @WebMethod
    void eliminarUsuario(@WebParam(name = "usuario") String usuario);
}