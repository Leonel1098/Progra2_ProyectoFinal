package com.infracciones.web.controller;

import com.infracciones.web.config.SesionAtributos;
import com.infracciones.web.dto.response.SesionUsuario;
import com.infracciones.web.service.AdminVehiculosService;
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
public class AdminVehiculosController {
    private static final Pattern PLACA = Pattern.compile("^[PMCA]\\d{3}[A-Z]{3}$");
    private final AdminVehiculosService vehiculos;

    public AdminVehiculosController(AdminVehiculosService vehiculos) { this.vehiculos = vehiculos; }

    @GetMapping("/admin/vehiculos")
    public String listar(@RequestParam(defaultValue = "") String placa, HttpSession sesion, Model model) {
        model.addAttribute("seccion", "Vehículos"); model.addAttribute("activeTab", "vehiculos"); model.addAttribute("basePath", "/admin/vehiculos"); model.addAttribute("vehiculos", List.of()); model.addAttribute("busqueda", placa);
        model.addAttribute("registros", List.of()); model.addAttribute("paramBusqueda", "placa"); model.addAttribute("placeholderBusqueda", "Buscar por placa (P123ABC)");
        try { var filas = placa.isBlank() ? vehiculos.listar(token(sesion)) : vehiculos.consultar(normalizar(placa), token(sesion)); model.addAttribute("vehiculos", filas); model.addAttribute("registros", filas); }
        catch (SoapFaultException e) { model.addAttribute("error", e.getMessage()); }
        catch (RuntimeException e) { model.addAttribute("error", "No se pudo cargar la información de vehículos."); }
        return "admin-registros";
    }

    @PostMapping("/admin/vehiculos/crear")
    public String crear(@RequestParam String placa, @RequestParam String marca, @RequestParam String modelo,
                        @RequestParam String color, @RequestParam(name = "_csrf", defaultValue = "") String csrf,
                        HttpSession sesion, RedirectAttributes redirect) {
        String p = normalizar(placa);
        if (!csrfValido(sesion, csrf)) return fallar(redirect, "El formulario venció. Inténtalo de nuevo.");
        if (!PLACA.matcher(p).matches() || marca.isBlank() || modelo.isBlank() || color.isBlank()) return fallar(redirect, "Completa placa válida, marca, modelo y color.");
        try { vehiculos.crear(p, marca.trim(), modelo.trim(), color.trim(), token(sesion)); redirect.addFlashAttribute("exito", "Vehículo creado correctamente."); }
        catch (SoapFaultException e) { return fallar(redirect, e.getMessage()); }
        catch (RuntimeException e) { return fallar(redirect, "No se pudo crear el vehículo."); }
        return "redirect:/admin/vehiculos";
    }

    @PostMapping("/admin/vehiculos/actualizar")
    public String actualizar(@RequestParam String placa, @RequestParam String marca, @RequestParam String modelo,
                             @RequestParam String color, @RequestParam(name = "_csrf", defaultValue = "") String csrf,
                             HttpSession sesion, RedirectAttributes redirect) {
        String p = normalizar(placa);
        if (!csrfValido(sesion, csrf)) return fallar(redirect, "El formulario venció. Inténtalo de nuevo.");
        if (!PLACA.matcher(p).matches() || marca.isBlank() || modelo.isBlank() || color.isBlank()) return fallar(redirect, "Completa placa válida, marca, modelo y color.");
        try { vehiculos.actualizar(p, marca.trim(), modelo.trim(), color.trim(), token(sesion)); redirect.addFlashAttribute("exito", "Vehículo actualizado correctamente."); }
        catch (SoapFaultException e) { return fallar(redirect, e.getMessage()); }
        catch (RuntimeException e) { return fallar(redirect, "No se pudo actualizar el vehículo."); }
        return "redirect:/admin/vehiculos";
    }

    @PostMapping("/admin/vehiculos/eliminar")
    public String eliminar(@RequestParam String placa, @RequestParam(name = "_csrf", defaultValue = "") String csrf,
                           HttpSession sesion, RedirectAttributes redirect) {
        if (!csrfValido(sesion, csrf)) return fallar(redirect, "El formulario venció. Inténtalo de nuevo.");
        try { vehiculos.eliminar(normalizar(placa), token(sesion)); redirect.addFlashAttribute("exito", "Vehículo eliminado."); }
        catch (SoapFaultException e) { return fallar(redirect, e.getMessage()); }
        catch (RuntimeException e) { return fallar(redirect, "No se pudo eliminar el vehículo."); }
        return "redirect:/admin/vehiculos";
    }

    private String normalizar(String placa) { return placa.replaceAll("\\s+", "").toUpperCase(); }
    private String fallar(RedirectAttributes redirect, String mensaje) { redirect.addFlashAttribute("error", mensaje); return "redirect:/admin/vehiculos"; }
    private String token(HttpSession sesion) { return ((SesionUsuario) sesion.getAttribute(SesionAtributos.USUARIO)).getToken(); }
    private boolean csrfValido(HttpSession sesion, String csrf) { Object esperado = sesion.getAttribute(SesionAtributos.CSRF); return esperado instanceof String v && MessageDigest.isEqual(v.getBytes(StandardCharsets.UTF_8), csrf.getBytes(StandardCharsets.UTF_8)); }
}
