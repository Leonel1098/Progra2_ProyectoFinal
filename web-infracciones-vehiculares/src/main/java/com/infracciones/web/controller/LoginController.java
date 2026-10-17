package com.infracciones.web.controller;

import com.infracciones.web.config.SesionAtributos;
import com.infracciones.web.dto.response.SesionUsuario;
import com.infracciones.web.service.AutenticacionService;
import com.infracciones.web.service.CredencialesInvalidasException;
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
import java.util.regex.Pattern;

@Controller
public class LoginController {

    private static final Pattern NUMERO = Pattern.compile("^[A-Z0-9-]{3,30}$");
    private static final SecureRandom ALEATORIO = new SecureRandom();

    private final AutenticacionService autenticacion;

    public LoginController(AutenticacionService autenticacion) {
        this.autenticacion = autenticacion;
    }

    @GetMapping("/login")
    public String mostrarLogin(@RequestParam(required = false) String redirect,
                               @RequestParam(required = false) String infraccionId,
                               HttpServletRequest request,
                               Model model) {

        HttpSession sesion = request.getSession();

        if (sesion.getAttribute(SesionAtributos.USUARIO) != null) {
            recordarPago(sesion, redirect, infraccionId);
            return "redirect:/inicio";
        }

        prepararFormulario(sesion, redirect, infraccionId, model);
        return "login";
    }

    @PostMapping("/login")
    public String iniciarSesion(@RequestParam(defaultValue = "") String usuario,
                                @RequestParam(defaultValue = "") String contrasena,
                                @RequestParam(name = "_csrf", defaultValue = "") String csrf,
                                @RequestParam(required = false) String redirect,
                                @RequestParam(required = false) String infraccionId,
                                HttpServletRequest request,
                                Model model) {

        HttpSession actual = request.getSession(false);
        if (actual == null || !csrfValido(actual, csrf)) {
            return "redirect:/login";
        }

        String nombre = usuario.trim();
        prepararFormulario(actual, redirect, infraccionId, model);
        model.addAttribute("usuario", nombre);

        if (nombre.isEmpty() || contrasena.isEmpty()) {
            model.addAttribute("mensaje", "Ingresa tu usuario y tu contraseña.");
            return "login";
        }
        if (nombre.length() > 20 || contrasena.length() > 128) {
            model.addAttribute("mensaje", "Usuario o contraseña incorrectos.");
            return "login";
        }

        try {
            SesionUsuario autenticado = autenticacion.login(nombre, contrasena);

            request.changeSessionId();
            HttpSession sesion = request.getSession();
            sesion.setAttribute(SesionAtributos.USUARIO, autenticado);
            sesion.setAttribute(SesionAtributos.CSRF, nuevoCsrf());
            recordarPago(sesion, redirect, infraccionId);

            return "redirect:/inicio";

        } catch (CredencialesInvalidasException e) {
            model.addAttribute("mensaje", "Usuario o contraseña incorrectos.");
        } catch (ServicioInfraccionesException e) {
            model.addAttribute("mensaje",
                    "No pudimos iniciar sesión en este momento. Inténtalo más tarde.");
        }
        return "login";
    }

    @PostMapping("/logout")
    public String cerrarSesion(@RequestParam(name = "_csrf", defaultValue = "") String csrf,
                               HttpServletRequest request) {
        HttpSession sesion = request.getSession(false);
        if (sesion != null && csrfValido(sesion, csrf)) {
            if (sesion.getAttribute(SesionAtributos.USUARIO) instanceof SesionUsuario u) {
                autenticacion.logout(u.getToken());
            }
            sesion.invalidate();
        }
        return "redirect:/";
    }

    private void prepararFormulario(HttpSession sesion, String redirect, String infraccionId, Model model) {
        if (!(sesion.getAttribute(SesionAtributos.CSRF) instanceof String)) {
            sesion.setAttribute(SesionAtributos.CSRF, nuevoCsrf());
        }
        model.addAttribute("csrf", sesion.getAttribute(SesionAtributos.CSRF));
        model.addAttribute("redirect", redirect);
        model.addAttribute("infraccionId", infraccionId);
    }

    private void recordarPago(HttpSession sesion, String redirect, String infraccionId) {
        if ("pago".equals(redirect) && infraccionId != null) {
            String id = infraccionId.trim().toUpperCase();
            if (NUMERO.matcher(id).matches()) {
                sesion.setAttribute(SesionAtributos.INFRACCION_POR_PAGAR, id);
            }
        }
    }

    private boolean csrfValido(HttpSession sesion, String recibido) {
        Object esperado = sesion.getAttribute(SesionAtributos.CSRF);
        return esperado instanceof String valor
                && MessageDigest.isEqual(
                valor.getBytes(StandardCharsets.UTF_8),
                recibido.getBytes(StandardCharsets.UTF_8));
    }

    private String nuevoCsrf() {
        byte[] bytes = new byte[32];
        ALEATORIO.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}