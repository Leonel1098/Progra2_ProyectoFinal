package com.infracciones.web.config;

import com.infracciones.web.dto.response.SesionUsuario;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class SesionInterceptor implements HandlerInterceptor {

    private static final Map<String, Set<String>> ROLES_POR_PREFIJO = Map.of(
            "/admin", Set.of("Admin"),
            "/agente", Set.of("Agente", "Admin"),
            "/ciudadano", Set.of("Ciudadano"));

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws IOException {

        HttpSession sesion = request.getSession(false);
        SesionUsuario usuario = null;
        if (sesion != null && sesion.getAttribute(SesionAtributos.USUARIO) instanceof SesionUsuario u) {
            usuario = u;
        }

        if (usuario == null || usuario.haExpirado()) {
            if (sesion != null) {
                sesion.invalidate();
            }
            response.sendRedirect(request.getContextPath() + "/login");
            return false;
        }

        String ruta = request.getRequestURI().substring(request.getContextPath().length());
        for (Map.Entry<String, Set<String>> regla : ROLES_POR_PREFIJO.entrySet()) {
            String prefijo = regla.getKey();
            boolean aplica = ruta.equals(prefijo) || ruta.startsWith(prefijo + "/");
            if (aplica && !regla.getValue().contains(usuario.getNombreRol())) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return false;
            }
        }

        response.setHeader("Cache-Control", "no-store");
        return true;
    }
}