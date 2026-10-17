package com.infracciones.web.config;

import com.infracciones.web.service.AccesoDenegadoException;
import com.infracciones.web.service.SesionInvalidaException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;

@ControllerAdvice
public class ManejadorErrores {

    // El token ya no es válido en el backend (reinicio o expiración): se cierra la sesión web.
    @ExceptionHandler(SesionInvalidaException.class)
    public String sesionInvalida(HttpServletRequest request) {
        HttpSession sesion = request.getSession(false);
        if (sesion != null) {
            sesion.invalidate();
        }
        return "redirect:/login";
    }

    @ExceptionHandler(AccesoDenegadoException.class)
    public void accesoDenegado(HttpServletResponse response) throws IOException {
        response.sendError(HttpServletResponse.SC_FORBIDDEN);
    }
}
