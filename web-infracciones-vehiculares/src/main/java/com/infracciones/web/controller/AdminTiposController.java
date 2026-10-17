package com.infracciones.web.controller;

import com.infracciones.web.config.SesionAtributos;
import com.infracciones.web.dto.response.SesionUsuario;
import com.infracciones.web.service.AdminTiposInfraccionService;
import com.infracciones.web.service.SoapFaultException;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;
import java.util.Map;

@Controller
public class AdminTiposController {
    private final AdminTiposInfraccionService tipos;
    public AdminTiposController(AdminTiposInfraccionService tipos) { this.tipos = tipos; }

    @GetMapping("/admin/tipos-infraccion")
    public String listar(@RequestParam(defaultValue = "") String codigo, HttpSession sesion, Model model) {
        model.addAttribute("seccion", "Tipos de infracción"); model.addAttribute("activeTab", "tipos"); model.addAttribute("basePath", "/admin/tipos-infraccion"); model.addAttribute("tipos", List.of()); model.addAttribute("busqueda", codigo);
        model.addAttribute("registros", List.of()); model.addAttribute("paramBusqueda", "codigo"); model.addAttribute("placeholderBusqueda", "Buscar por código");
        try { var filas = codigo.isBlank() ? tipos.listar(token(sesion)) : tipos.porCodigo(codigo.trim(), token(sesion)); model.addAttribute("tipos", filas); model.addAttribute("registros", filas); }
        catch (SoapFaultException e) { model.addAttribute("error", e.getMessage()); }
        catch (RuntimeException e) { model.addAttribute("error", "No se pudo cargar el catálogo de infracciones."); }
        return "admin-registros";
    }

    @PostMapping("/admin/tipos-infraccion/crear")
    public String crear(@RequestParam String codigo, @RequestParam String nombre, @RequestParam String descripcion,
                        @RequestParam String monto, @RequestParam(name = "_csrf", defaultValue = "") String csrf,
                        HttpSession sesion, RedirectAttributes redirect) {
        if (!csrfValido(sesion, csrf)) return fallo(redirect, "El formulario venció. Inténtalo de nuevo.");
        if (!datosValidos(codigo, nombre, monto)) return fallo(redirect, "Completa código, nombre y un monto válido mayor o igual a cero.");
        try { tipos.crear(codigo.trim(), nombre.trim(), descripcion.trim(), monto.trim(), token(sesion)); redirect.addFlashAttribute("exito", "Tipo de infracción creado."); }
        catch (SoapFaultException e) { return fallo(redirect, e.getMessage()); }
        catch (RuntimeException e) { return fallo(redirect, "No se pudo crear el tipo de infracción."); }
        return "redirect:/admin/tipos-infraccion";
    }

    @PostMapping("/admin/tipos-infraccion/actualizar")
    public String actualizar(@RequestParam String id, @RequestParam String codigo, @RequestParam String nombre,
                             @RequestParam String descripcion, @RequestParam String monto,
                             @RequestParam(name = "_csrf", defaultValue = "") String csrf,
                             HttpSession sesion, RedirectAttributes redirect) {
        if (!csrfValido(sesion, csrf)) return fallo(redirect, "El formulario venció. Inténtalo de nuevo.");
        if (!enteroPositivo(id) || !datosValidos(codigo, nombre, monto)) return fallo(redirect, "Revisa el ID, código, nombre y monto del tipo.");
        try { tipos.actualizar(id, codigo.trim(), nombre.trim(), descripcion.trim(), monto.trim(), token(sesion)); redirect.addFlashAttribute("exito", "Tipo de infracción actualizado."); }
        catch (SoapFaultException e) { return fallo(redirect, e.getMessage()); }
        catch (RuntimeException e) { return fallo(redirect, "No se pudo actualizar el tipo de infracción."); }
        return "redirect:/admin/tipos-infraccion";
    }

    @PostMapping("/admin/tipos-infraccion/eliminar")
    public String eliminar(@RequestParam String id, @RequestParam(name = "_csrf", defaultValue = "") String csrf,
                           HttpSession sesion, RedirectAttributes redirect) {
        if (!csrfValido(sesion, csrf)) return fallo(redirect, "El formulario venció. Inténtalo de nuevo.");
        if (!enteroPositivo(id)) return fallo(redirect, "ID de tipo no válido.");
        try { tipos.eliminar(id, token(sesion)); redirect.addFlashAttribute("exito", "Tipo de infracción eliminado."); }
        catch (SoapFaultException e) { return fallo(redirect, e.getMessage()); }
        catch (RuntimeException e) { return fallo(redirect, "No se pudo eliminar el tipo de infracción."); }
        return "redirect:/admin/tipos-infraccion";
    }

    private boolean datosValidos(String codigo, String nombre, String monto) {
        try { return !codigo.isBlank() && codigo.length() <= 30 && !nombre.isBlank() && nombre.length() <= 120 && new BigDecimal(monto).compareTo(BigDecimal.ZERO) >= 0; }
        catch (RuntimeException e) { return false; }
    }
    private boolean enteroPositivo(String valor) { try { return Integer.parseInt(valor) > 0; } catch (RuntimeException e) { return false; } }
    private String fallo(RedirectAttributes redirect, String mensaje) { redirect.addFlashAttribute("error", mensaje); return "redirect:/admin/tipos-infraccion"; }
    private String token(HttpSession sesion) { return ((SesionUsuario) sesion.getAttribute(SesionAtributos.USUARIO)).getToken(); }
    private boolean csrfValido(HttpSession sesion, String csrf) { Object esperado = sesion.getAttribute(SesionAtributos.CSRF); return esperado instanceof String v && MessageDigest.isEqual(v.getBytes(StandardCharsets.UTF_8), csrf.getBytes(StandardCharsets.UTF_8)); }
}
