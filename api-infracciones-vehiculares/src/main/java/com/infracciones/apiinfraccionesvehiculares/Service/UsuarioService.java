package com.infracciones.apiinfraccionesvehiculares.Service;

import com.infracciones.apiinfraccionesvehiculares.Excepcion.PersonaNoEncontradaException;
import com.infracciones.apiinfraccionesvehiculares.Excepcion.PersonaYaTieneUsuarioException;
import com.infracciones.apiinfraccionesvehiculares.Excepcion.RolNoEncontradoException;
import com.infracciones.apiinfraccionesvehiculares.Excepcion.UsuarioDuplicadoException;
import com.infracciones.apiinfraccionesvehiculares.Excepcion.UsuarioEnUsoException;
import com.infracciones.apiinfraccionesvehiculares.Excepcion.UsuarioNoEncontradoException;
import com.infracciones.apiinfraccionesvehiculares.Model.Persona;
import com.infracciones.apiinfraccionesvehiculares.Model.Usuario;
import com.infracciones.apiinfraccionesvehiculares.Repository.PersonaRepository;
import com.infracciones.apiinfraccionesvehiculares.Repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PersonaRepository personaRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PersonaRepository personaRepository,
                          PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.personaRepository = personaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Usuario> listAll() {
        return usuarioRepository.findAll();
    }

    public Usuario getByUsername(String usuario) {
        return usuarioRepository.findByUsername(usuario)
                .orElseThrow(() -> new UsuarioNoEncontradoException(
                        "No existe un usuario con nombre: " + usuario));
    }

    public Usuario createUser(String dpiPersona, String nombreUsuario, String contrasena,
                              String nombreRol, Boolean estado) {
        if (dpiPersona == null || dpiPersona.isBlank()) {
            throw new IllegalArgumentException("El DPI de la persona es obligatorio.");
        }
        if (nombreUsuario == null || nombreUsuario.isBlank()) {
            throw new IllegalArgumentException("El nombre de usuario es obligatorio.");
        }
        if (contrasena == null || contrasena.isBlank()) {
            throw new IllegalArgumentException("La contraseña es obligatoria.");
        }
        if (nombreRol == null || nombreRol.isBlank()) {
            throw new IllegalArgumentException("El rol es obligatorio.");
        }

        Persona persona = personaRepository.findByDpi(dpiPersona)
                .orElseThrow(() -> new PersonaNoEncontradaException(
                        "No existe una persona con DPI: " + dpiPersona));

        if (usuarioRepository.existsByIdPersona(persona.getIdPersona())) {
            throw new PersonaYaTieneUsuarioException(
                    "La persona con DPI " + dpiPersona + " ya tiene un usuario asociado.");
        }

        if (usuarioRepository.existsByUsername(nombreUsuario)) {
            throw new UsuarioDuplicadoException(
                    "Ya existe un usuario con el nombre: " + nombreUsuario);
        }

        Integer idRol = usuarioRepository.findIdRolByNombre(nombreRol)
                .orElseThrow(() -> new RolNoEncontradoException("No existe el rol: " + nombreRol));

        Usuario nuevo = new Usuario();
        nuevo.setUsuario(nombreUsuario);
        nuevo.setContrasenaHash(passwordEncoder.encode(contrasena));
        nuevo.setEstado(estado != null ? estado : Boolean.TRUE);
        nuevo.setIdPersona(persona.getIdPersona());
        nuevo.setIdRol(idRol);

        usuarioRepository.createUser(nuevo);
        return getByUsername(nombreUsuario);
    }

    public Usuario updateUser(String usuario, String nombreRol, Boolean estado) {
        Usuario existente = getByUsername(usuario);

        Integer idRol = existente.getIdRol();
        if (nombreRol != null && !nombreRol.isBlank()) {
            idRol = usuarioRepository.findIdRolByNombre(nombreRol)
                    .orElseThrow(() -> new RolNoEncontradoException("No existe el rol: " + nombreRol));
        }

        Boolean nuevoEstado = estado != null ? estado : existente.getEstado();

        usuarioRepository.updateByUsername(usuario, idRol, nuevoEstado);
        return getByUsername(usuario);
    }

    public void updatePassword(String usuario, String nuevaContrasena) {
        getByUsername(usuario);
        if (nuevaContrasena == null || nuevaContrasena.isBlank()) {
            throw new IllegalArgumentException("La nueva contraseña es obligatoria.");
        }
        usuarioRepository.updatePasswordByUsername(usuario, passwordEncoder.encode(nuevaContrasena));
    }

    public void deleteUser(String usuario) {
        Usuario existente = getByUsername(usuario);

        if (usuarioRepository.tieneInfraccionesAsociadas(existente.getIdUsuario())) {
            throw new UsuarioEnUsoException(
                    "No se puede eliminar el usuario '" + usuario +
                            "' porque tiene infracciones registradas a su nombre.");
        }

        usuarioRepository.deleteByUsername(usuario);
    }
}