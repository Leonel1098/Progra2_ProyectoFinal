package com.infracciones.apiinfraccionesvehiculares.Service;

import com.infracciones.apiinfraccionesvehiculares.Excepcion.AccesoDenegadoException;
import com.infracciones.apiinfraccionesvehiculares.Excepcion.NoAutenticadoException;
import com.infracciones.apiinfraccionesvehiculares.Model.Sesion;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;

@Component
public class SeguridadService {

    public static final String ADMIN = "Admin";
    public static final String AGENTE = "Agente";
    public static final String CIUDADANO = "Ciudadano";

    private final SesionService sesionService;

    public SeguridadService(SesionService sesionService) {
        this.sesionService = sesionService;
    }

    public String tokenActual() {
        if (!(RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attrs)) {
            return null;
        }
        String header = attrs.getRequest().getHeader("Authorization");
        if (header == null || !header.regionMatches(true, 0, "Bearer ", 0, 7)) {
            return null;
        }
        return header.substring(7).trim();
    }

    public Sesion requerirSesion() {
        return sesionService.validar(tokenActual())
                .orElseThrow(() -> new NoAutenticadoException(
                        "Debe iniciar sesión (header Authorization: Bearer <token>)."));
    }

    public Sesion requerirRol(String... rolesPermitidos) {
        Sesion sesion = requerirSesion();
        boolean permitido = Arrays.stream(rolesPermitidos)
                .anyMatch(r -> r.equalsIgnoreCase(sesion.getNombreRol()));
        if (!permitido) {
            throw new AccesoDenegadoException("Su rol no tiene permiso para esta operación.");
        }
        return sesion;
    }

    // Un Ciudadano solo puede operar sobre su propio DPI; Admin y Agente no tienen restricción.
    public void requerirPropietarioSiCiudadano(Sesion sesion, String dpiDueno) {
        if (CIUDADANO.equalsIgnoreCase(sesion.getNombreRol())
                && (dpiDueno == null || !dpiDueno.equals(sesion.getDpiPersona()))) {
            throw new AccesoDenegadoException("Solo puede acceder a su propia información.");
        }
    }

    public void cerrarSesion() {
        sesionService.revocar(tokenActual());
    }
}