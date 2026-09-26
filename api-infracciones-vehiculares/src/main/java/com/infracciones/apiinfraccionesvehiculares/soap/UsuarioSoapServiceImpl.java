package com.infracciones.apiinfraccionesvehiculares.soap;

import com.infracciones.apiinfraccionesvehiculares.Model.Usuario;
import com.infracciones.apiinfraccionesvehiculares.Service.UsuarioService;
import jakarta.jws.WebService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@WebService(
        serviceName = "UsuarioSoapService",
        endpointInterface = "com.infracciones.apiinfraccionesvehiculares.soap.UsuarioSoapService",
        targetNamespace = "https://soap.apiinfraccionesvehiculares.infracciones.com/"
)
public class UsuarioSoapServiceImpl implements UsuarioSoapService {

    private final UsuarioService usuarioService;

    public UsuarioSoapServiceImpl(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Override
    public List<Usuario> listarUsuarios() {
        return usuarioService.listAll();
    }

    @Override
    public Usuario consultarUsuarioPorUsername(String usuario) {
        return usuarioService.getByUsername(usuario);
    }

    @Override
    public Usuario crearUsuario(String dpiPersona, String usuario, String contrasena,
                                String nombreRol, Boolean estado) {
        return usuarioService.createUser(dpiPersona, usuario, contrasena, nombreRol, estado);
    }

    @Override
    public Usuario actualizarUsuario(String usuario, String nombreRol, Boolean estado) {
        return usuarioService.updateUser(usuario, nombreRol, estado);
    }

    @Override
    public void cambiarContrasena(String usuario, String nuevaContrasena) {
        usuarioService.updatePassword(usuario, nuevaContrasena);
    }

    @Override
    public void eliminarUsuario(String usuario) {
        usuarioService.deleteUser(usuario);
    }
}