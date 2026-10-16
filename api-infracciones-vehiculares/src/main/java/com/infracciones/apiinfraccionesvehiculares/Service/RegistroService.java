package com.infracciones.apiinfraccionesvehiculares.Service;

import com.infracciones.apiinfraccionesvehiculares.Excepcion.CredencialesInvalidasException;
import com.infracciones.apiinfraccionesvehiculares.Model.Persona;
import com.infracciones.apiinfraccionesvehiculares.Model.Usuario;
import com.infracciones.apiinfraccionesvehiculares.Repository.PersonaRepository;
import com.infracciones.apiinfraccionesvehiculares.Repository.UsuarioRepository;
import org.springframework.stereotype.Service;

@Service
public class RegistroService {

    private static final String MENSAJE_GENERICO =
            "No fue posible crear la cuenta con los datos proporcionados.";

    private final PersonaRepository personaRepository;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioService usuarioService;

    public RegistroService(PersonaRepository personaRepository,
                           UsuarioRepository usuarioRepository,
                           UsuarioService usuarioService) {
        this.personaRepository = personaRepository;
        this.usuarioRepository = usuarioRepository;
        this.usuarioService = usuarioService;
    }

    public Usuario registrarCuenta(String dpi, String nombre, String apellido,
                                   String nombreUsuario, String contrasena) {
        if (dpi == null || dpi.isBlank() || nombre == null || nombre.isBlank()
                || apellido == null || apellido.isBlank()) {
            throw new IllegalArgumentException("DPI, nombre y apellido son obligatorios.");
        }

        // Persona inexistente, datos que no coinciden o cuenta ya creada: mismo mensaje
        // para no revelar qué DPIs existen.
        Persona persona = personaRepository.findByDpi(dpi.trim())
                .orElseThrow(() -> new CredencialesInvalidasException(MENSAJE_GENERICO));

        boolean coincide = persona.getNombrePersona().trim().equalsIgnoreCase(nombre.trim())
                && persona.getApellidoPersona().trim().equalsIgnoreCase(apellido.trim());
        if (!coincide || usuarioRepository.existsByIdPersona(persona.getIdPersona())) {
            throw new CredencialesInvalidasException(MENSAJE_GENERICO);
        }

        return usuarioService.createUser(persona.getDpiPersona(), nombreUsuario,
                contrasena, SeguridadService.CIUDADANO, Boolean.TRUE);
    }
}