package com.infracciones.web.controller;

import com.infracciones.web.config.SesionAtributos;
import com.infracciones.web.dto.response.SesionUsuario;
import com.infracciones.web.service.AdminPersonasService;
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
import java.util.Map;
import java.util.regex.Pattern;

@Controller
public class AdminPersonasController {
    private static final Pattern DPI = Pattern.compile("^\\d{13}$");
    private final AdminPersonasService personas;

    public AdminPersonasController(AdminPersonasService personas) { this.personas = personas; }

    @GetMapping("/admin/personas")
    public String listar(@RequestParam(defaultValue = "") String dpi, HttpSession sesion, Model model) {
        model.addAttribute("seccion", "Personas");
        model.addAttribute("activeTab", "personas");
        model.addAttribute("basePath", "/admin/personas");
        model.addAttribute("personas", List.of());
        model.addAttribute("dpi", dpi);
        model.addAttribute("registros", List.of());
        model.addAttribute("paramBusqueda", "dpi");
        model.addAttribute("placeholderBusqueda", "Buscar por DPI");
        try {
            List<Map<String, String>> filas = dpi.isBlank() ? personas.listar(token(sesion)) : personas.consultar(dpi.replaceAll("\\D", ""), token(sesion));
            model.addAttribute("personas", filas);
            model.addAttribute("registros", filas);
        } catch (SoapFaultException e) { model.addAttribute("error", e.getMessage()); }
        catch (RuntimeException e) { model.addAttribute("error", "No se pudo cargar la información de personas."); }
        return "admin-registros";
    }

    @PostMapping("/admin/personas/crear")
    public String crear(@RequestParam String dpi, @RequestParam String nombre, @RequestParam String apellido,
                        @RequestParam(defaultValue = "") String telefono, @RequestParam(defaultValue = "") String direccion,
                        @RequestParam(name = "_csrf", defaultValue = "") String csrf, HttpSession sesion, RedirectAttributes redirect) {
        if (!csrfValido(sesion, csrf)) return fallar(redirect, "El formulario venció. Inténtalo de nuevo.");
        String normalizado = dpi.replaceAll("\\D", "");
        if (!DPI.matcher(normalizado).matches() || nombre.isBlank() || apellido.isBlank()) return fallar(redirect, "Ingresa DPI válido, nombre y apellido.");
        try { personas.crear(normalizado, nombre.trim(), apellido.trim(), telefono.trim(), direccion.trim(), token(sesion)); redirect.addFlashAttribute("exito", "Persona creada correctamente."); }
        catch (SoapFaultException e) { return fallar(redirect, e.getMessage()); }
        catch (RuntimeException e) { return fallar(redirect, "No se pudo crear la persona."); }
        return "redirect:/admin/personas";
    }

    @PostMapping("/admin/personas/actualizar")
    public String actualizar(@RequestParam String dpi, @RequestParam String nombre, @RequestParam String apellido,
                             @RequestParam(defaultValue = "") String telefono, @RequestParam(defaultValue = "") String direccion,
                             @RequestParam(name = "_csrf", defaultValue = "") String csrf, HttpSession sesion, RedirectAttributes redirect) {
        if (!csrfValido(sesion, csrf)) return fallar(redirect, "El formulario venció. Inténtalo de nuevo.");
        if (!DPI.matcher(dpi.replaceAll("\\D", "")).matches() || nombre.isBlank() || apellido.isBlank()) return fallar(redirect, "Ingresa DPI válido, nombre y apellido.");
        try { personas.actualizar(dpi.replaceAll("\\D", ""), nombre.trim(), apellido.trim(), telefono.trim(), direccion.trim(), token(sesion)); redirect.addFlashAttribute("exito", "Persona actualizada correctamente."); }
        catch (SoapFaultException e) { return fallar(redirect, e.getMessage()); }
        catch (RuntimeException e) { return fallar(redirect, "No se pudo actualizar la persona."); }
        return "redirect:/admin/personas";
    }

    @PostMapping("/admin/personas/eliminar")
    public String eliminar(@RequestParam String dpi, @RequestParam(name = "_csrf", defaultValue = "") String csrf,
                           HttpSession sesion, RedirectAttributes redirect) {
        if (!csrfValido(sesion, csrf)) return fallar(redirect, "El formulario venció. Inténtalo de nuevo.");
        try { personas.eliminar(dpi, token(sesion)); redirect.addFlashAttribute("exito", "Persona eliminada."); }
        catch (SoapFaultException e) { return fallar(redirect, e.getMessage()); }
        catch (RuntimeException e) { return fallar(redirect, "No se pudo eliminar la persona."); }
        return "redirect:/admin/personas";
    }

    private String fallar(RedirectAttributes redirect, String mensaje) { redirect.addFlashAttribute("error", mensaje); return "redirect:/admin/personas"; }
    private String token(HttpSession sesion) { return ((SesionUsuario) sesion.getAttribute(SesionAtributos.USUARIO)).getToken(); }
    private boolean csrfValido(HttpSession sesion, String csrf) { Object esperado = sesion.getAttribute(SesionAtributos.CSRF); return esperado instanceof String v && MessageDigest.isEqual(v.getBytes(StandardCharsets.UTF_8), csrf.getBytes(StandardCharsets.UTF_8)); }
}
