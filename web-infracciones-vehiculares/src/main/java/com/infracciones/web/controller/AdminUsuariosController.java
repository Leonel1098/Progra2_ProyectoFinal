package com.infracciones.web.controller;

import com.infracciones.web.config.SesionAtributos;
import com.infracciones.web.dto.response.SesionUsuario;
import com.infracciones.web.service.AdminPersonasService;
import com.infracciones.web.service.AdminUsuariosService;
import com.infracciones.web.service.SoapFaultException;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;
import java.util.Set;

@Controller
public class AdminUsuariosController {
    private static final Set<String> ROLES = Set.of("Admin", "Agente", "Ciudadano");
    private final AdminUsuariosService usuarios;
    private final AdminPersonasService personas;

    public AdminUsuariosController(AdminUsuariosService usuarios, AdminPersonasService personas) {
        this.usuarios = usuarios;
        this.personas = personas;
    }

    @GetMapping("/admin/usuarios")
    public String listar(@RequestParam(defaultValue = "") String q, HttpSession sesion, Model model) {
        model.addAttribute("seccion", "Usuarios y roles");
        model.addAttribute("activeTab", "usuarios");
        model.addAttribute("basePath", "/admin/usuarios");
        model.addAttribute("busqueda", q);
        model.addAttribute("usuarios", List.of());
        model.addAttribute("personas", List.of());
        model.addAttribute("roles", ROLES);
        model.addAttribute("registros", List.of()); model.addAttribute("paramBusqueda", "q"); model.addAttribute("placeholderBusqueda", "Buscar por nombre de usuario");
        try {
            var filas = q.isBlank() ? usuarios.listar(token(sesion)) : usuarios.consultar(q.trim(), token(sesion));
            model.addAttribute("usuarios", filas); model.addAttribute("registros", filas);
        } catch (SoapFaultException e) {
            model.addAttribute("error", e.getMessage());
        } catch (RuntimeException e) {
            model.addAttribute("error", "No se pudo cargar el listado de usuarios.");
        }
        try {
            model.addAttribute("personas", personas.listar(token(sesion)));
        } catch (RuntimeException e) {
            model.addAttribute("errorPersonas", "No se pudo cargar el listado de personas para crear usuarios.");
        }
        return "admin-registros";
    }

    @PostMapping("/admin/usuarios/crear")
    public String crear(@RequestParam String dpiPersona, @RequestParam String usuario,
                        @RequestParam String contrasena, @RequestParam String nombreRol,
                        @RequestParam(defaultValue = "true") String estado,
                        @RequestParam(name = "_csrf", defaultValue = "") String csrf,
                        HttpSession sesion, RedirectAttributes redirect) {
        if (!csrfValido(sesion, csrf)) return fallo(redirect, "El formulario venció. Inténtalo de nuevo.");
        if (dpiPersona.isBlank() || usuario.isBlank() || usuario.length() > 20
                || contrasena.length() < 8 || contrasena.length() > 128
                || !ROLES.contains(nombreRol) || !booleano(estado)) {
            return fallo(redirect, "Completa DPI, usuario, contraseña de 8 a 128 caracteres, rol y estado.");
        }
        try {
            usuarios.crear(dpiPersona, usuario.trim(), contrasena, nombreRol, estado, token(sesion));
            redirect.addFlashAttribute("exito", "Usuario creado.");
        } catch (SoapFaultException e) {
            return fallo(redirect, e.getMessage());
        } catch (RuntimeException e) {
            return fallo(redirect, "No se pudo crear el usuario.");
        }
        return "redirect:/admin/usuarios";
    }

    @PostMapping("/admin/usuarios/actualizar")
    public String actualizar(@RequestParam String usuario, @RequestParam String nombreRol,
                             @RequestParam String estado, @RequestParam(name = "_csrf", defaultValue = "") String csrf,
                             HttpSession sesion, RedirectAttributes redirect) {
        if (!csrfValido(sesion, csrf)) return fallo(redirect, "El formulario venció. Inténtalo de nuevo.");
        if (usuario.isBlank() || !ROLES.contains(nombreRol) || !booleano(estado)) return fallo(redirect, "Revisa el usuario, rol y estado.");
        try {
            usuarios.actualizar(usuario, nombreRol, estado, token(sesion));
            redirect.addFlashAttribute("exito", "Usuario actualizado.");
        } catch (SoapFaultException e) {
            return fallo(redirect, e.getMessage());
        } catch (RuntimeException e) {
            return fallo(redirect, "No se pudo actualizar el usuario.");
        }
        return "redirect:/admin/usuarios";
    }

    @PostMapping("/admin/usuarios/contrasena")
    public String cambiarContrasena(@RequestParam String usuario, @RequestParam String nuevaContrasena,
                                    @RequestParam String confirmarContrasena,
                                    @RequestParam(name = "_csrf", defaultValue = "") String csrf,
                                    HttpSession sesion, RedirectAttributes redirect) {
        if (!csrfValido(sesion, csrf)) return fallo(redirect, "El formulario venció. Inténtalo de nuevo.");
        if (usuario.isBlank() || nuevaContrasena.length() < 8 || nuevaContrasena.length() > 128) {
            return fallo(redirect, "La nueva contraseña debe tener entre 8 y 128 caracteres.");
        }
        if (!nuevaContrasena.equals(confirmarContrasena)) {
            return fallo(redirect, "La confirmación no coincide con la nueva contraseña.");
        }
        try {
            usuarios.cambiarContrasena(usuario, nuevaContrasena, token(sesion));
            redirect.addFlashAttribute("exito", "Contraseña actualizada para el usuario " + usuario + ".");
        } catch (SoapFaultException e) {
            return fallo(redirect, e.getMessage());
        } catch (RuntimeException e) {
            return fallo(redirect, "No se pudo actualizar la contraseña.");
        }
        return "redirect:/admin/usuarios";
    }

    @PostMapping("/admin/usuarios/eliminar")
    public String eliminar(@RequestParam String usuario, @RequestParam(name = "_csrf", defaultValue = "") String csrf,
                           HttpSession sesion, RedirectAttributes redirect) {
        if (!csrfValido(sesion, csrf)) return fallo(redirect, "El formulario venció. Inténtalo de nuevo.");
        if (usuario.isBlank()) return fallo(redirect, "Usuario no válido.");
        SesionUsuario actual = (SesionUsuario) sesion.getAttribute(SesionAtributos.USUARIO);
        if (actual.getUsuario().equalsIgnoreCase(usuario)) return fallo(redirect, "No puedes eliminar el usuario de la sesión activa.");
        try {
            usuarios.eliminar(usuario, token(sesion));
            redirect.addFlashAttribute("exito", "Usuario eliminado.");
        } catch (SoapFaultException e) {
            return fallo(redirect, e.getMessage());
        } catch (RuntimeException e) {
            return fallo(redirect, "No se pudo eliminar el usuario.");
        }
        return "redirect:/admin/usuarios";
    }

    private boolean booleano(String valor) { return "true".equalsIgnoreCase(valor) || "false".equalsIgnoreCase(valor); }
    private String fallo(RedirectAttributes redirect, String mensaje) { redirect.addFlashAttribute("error", mensaje); return "redirect:/admin/usuarios"; }
    private String token(HttpSession sesion) { return ((SesionUsuario) sesion.getAttribute(SesionAtributos.USUARIO)).getToken(); }
    private boolean csrfValido(HttpSession sesion, String csrf) {
        Object esperado = sesion.getAttribute(SesionAtributos.CSRF);
        return esperado instanceof String valor && MessageDigest.isEqual(valor.getBytes(StandardCharsets.UTF_8), csrf.getBytes(StandardCharsets.UTF_8));
    }
}
