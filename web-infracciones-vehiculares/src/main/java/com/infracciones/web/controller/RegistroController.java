package com.infracciones.web.controller;

import com.infracciones.web.config.SesionAtributos;
import com.infracciones.web.service.AutenticacionService;
import com.infracciones.web.service.ServicioInfraccionesException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

@Controller
public class RegistroController {

    private static final SecureRandom ALEATORIO = new SecureRandom();
    private final AutenticacionService autenticacion;

    public RegistroController(AutenticacionService autenticacion) {
        this.autenticacion = autenticacion;
    }

    @GetMapping("/registro")
    public String mostrarRegistro(HttpServletRequest request, Model model) {
        prepararFormulario(request.getSession(), model);
        return "registro";
    }

    @PostMapping("/registro")
    public String registrar(@RequestParam(defaultValue = "") String dpi,
                            @RequestParam(defaultValue = "") String nombre,
                            @RequestParam(defaultValue = "") String apellido,
                            @RequestParam(defaultValue = "") String usuario,
                            @RequestParam(defaultValue = "") String contrasena,
                            @RequestParam(name = "_csrf", defaultValue = "") String csrf,
                            HttpServletRequest request, Model model) {
        HttpSession sesion = request.getSession(false);
        if (sesion == null || !csrfValido(sesion, csrf)) {
            return "redirect:/registro";
        }

        prepararFormulario(sesion, model);
        model.addAttribute("dpi", dpi.trim());
        model.addAttribute("nombre", nombre.trim());
        model.addAttribute("apellido", apellido.trim());
        model.addAttribute("usuario", usuario.trim());

        String nombreLimpio = nombre.trim();
        String apellidoLimpio = apellido.trim();
        String usuarioLimpio = usuario.trim();
        if (dpi.isBlank() || nombreLimpio.isEmpty() || apellidoLimpio.isEmpty()
                || usuarioLimpio.isEmpty() || contrasena.isEmpty()) {
            model.addAttribute("mensaje", "Completa todos los campos.");
            return "registro";
        }
        if (dpi.trim().length() > 20 || nombreLimpio.length() > 100
                || apellidoLimpio.length() > 100 || usuarioLimpio.length() > 20
                || contrasena.length() > 128) {
            model.addAttribute("mensaje", "Revisa la longitud de los datos ingresados.");
            return "registro";
        }

        try {
            autenticacion.registrarCuenta(dpi.trim(), nombreLimpio, apellidoLimpio,
                    usuarioLimpio, contrasena);
            return "redirect:/login?registro=ok";
        } catch (ServicioInfraccionesException e) {
            model.addAttribute("mensaje",
                    "No se pudo crear la cuenta. Verifica tus datos o inténtalo más tarde.");
            return "registro";
        }
    }

    private void prepararFormulario(HttpSession sesion, Model model) {
        if (!(sesion.getAttribute(SesionAtributos.CSRF) instanceof String)) {
            byte[] bytes = new byte[32];
            ALEATORIO.nextBytes(bytes);
            sesion.setAttribute(SesionAtributos.CSRF,
                    Base64.getUrlEncoder().withoutPadding().encodeToString(bytes));
        }
        model.addAttribute("csrf", sesion.getAttribute(SesionAtributos.CSRF));
    }

    private boolean csrfValido(HttpSession sesion, String recibido) {
        Object esperado = sesion.getAttribute(SesionAtributos.CSRF);
        return esperado instanceof String valor && MessageDigest.isEqual(
                valor.getBytes(StandardCharsets.UTF_8), recibido.getBytes(StandardCharsets.UTF_8));
    }
}
