package com.infracciones.apiinfraccionesvehiculares.Service;

import com.infracciones.apiinfraccionesvehiculares.Model.Sesion;
import com.infracciones.apiinfraccionesvehiculares.Model.Usuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SesionService {

    private final Map<String, Sesion> sesiones = new ConcurrentHashMap<>();
    private final SecureRandom random = new SecureRandom();
    private final long duracionMinutos;

    public SesionService(@Value("${app.sesion.duracion-minutos:120}") long duracionMinutos) {
        this.duracionMinutos = duracionMinutos;
    }

    public Sesion crear(Usuario usuario) {
        limpiarExpiradas();

        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);

        Sesion sesion = new Sesion(
                token,
                usuario.getUsuario(),
                usuario.getNombreRol(),
                usuario.getDpiPersona(),
                Instant.now().plus(Duration.ofMinutes(duracionMinutos))
        );
        sesiones.put(token, sesion);
        return sesion;
    }

    public Optional<Sesion> validar(String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }
        Sesion sesion = sesiones.get(token);
        if (sesion == null) {
            return Optional.empty();
        }
        if (sesion.estaExpirada()) {
            sesiones.remove(token);
            return Optional.empty();
        }
        return Optional.of(sesion);
    }

    public void revocar(String token) {
        if (token != null) {
            sesiones.remove(token);
        }
    }

    private void limpiarExpiradas() {
        sesiones.values().removeIf(Sesion::estaExpirada);
    }
}