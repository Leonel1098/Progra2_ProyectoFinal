package com.infracciones.apiinfraccionesvehiculares.Service;

import com.infracciones.apiinfraccionesvehiculares.Excepcion.CredencialesInvalidasException;
import com.infracciones.apiinfraccionesvehiculares.Model.LoginResultado;
import com.infracciones.apiinfraccionesvehiculares.Model.Persona;
import com.infracciones.apiinfraccionesvehiculares.Model.Sesion;
import com.infracciones.apiinfraccionesvehiculares.Model.Usuario;
import com.infracciones.apiinfraccionesvehiculares.Repository.PersonaRepository;
import com.infracciones.apiinfraccionesvehiculares.Repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private static final String MENSAJE_INVALIDO = "Usuario o contraseña incorrectos.";

    private final UsuarioRepository usuarioRepository;
    private final PersonaRepository personaRepository;
    private final PasswordEncoder passwordEncoder;
    private final SesionService sesionService;
    private final String hashFicticio;

    public AuthService(UsuarioRepository usuarioRepository,
                       PersonaRepository personaRepository,
                       PasswordEncoder passwordEncoder,
                       SesionService sesionService) {
        this.usuarioRepository = usuarioRepository;
        this.personaRepository = personaRepository;
        this.passwordEncoder = passwordEncoder;
        this.sesionService = sesionService;
        this.hashFicticio = passwordEncoder.encode("contrasena-ficticia");
    }

    public LoginResultado login(String nombreUsuario, String contrasena) {
        if (nombreUsuario == null || nombreUsuario.isBlank()
                || contrasena == null || contrasena.isBlank()) {
            throw new IllegalArgumentException("El usuario y la contraseña son obligatorios.");
        }

        Optional<Usuario> encontrado = usuarioRepository.findByUsername(nombreUsuario);

        String hash = encontrado.map(Usuario::getContrasenaHash).orElse(hashFicticio);
        boolean coincide = passwordEncoder.matches(contrasena, hash);

        if (encontrado.isEmpty() || !coincide || !Boolean.TRUE.equals(encontrado.get().getEstado())) {
            throw new CredencialesInvalidasException(MENSAJE_INVALIDO);
        }

        Usuario usuario = encontrado.get();
        Persona persona = personaRepository.findByDpi(usuario.getDpiPersona())
                .orElseThrow(() -> new IllegalStateException("El usuario no tiene una persona asociada."));

        Sesion sesion = sesionService.crear(usuario);

        LoginResultado resultado = new LoginResultado();
        resultado.setToken(sesion.getToken());
        resultado.setUsuario(usuario.getUsuario());
        resultado.setNombreRol(usuario.getNombreRol());
        resultado.setDpiPersona(usuario.getDpiPersona());
        resultado.setNombrePersona(persona.getNombrePersona() + " " + persona.getApellidoPersona());
        resultado.setExpiraEn(sesion.getExpiraEn().toString());
        return resultado;
    }
}