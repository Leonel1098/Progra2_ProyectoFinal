package com.infracciones.web.controller;

import com.infracciones.web.config.SesionAtributos;
import com.infracciones.web.dto.response.SesionUsuario;
import com.infracciones.web.service.AdminInfraccionesService;
import com.infracciones.web.service.SoapFaultException;
import com.infracciones.web.service.ServicioInfraccionesException;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Controller
public class AdminInfraccionesController {
    private static final int TAMANO_PAGINA = 12;
    private final AdminInfraccionesService infracciones;

    public AdminInfraccionesController(AdminInfraccionesService infracciones) {
        this.infracciones = infracciones;
    }

    @GetMapping("/admin/infracciones")
    public String listar(@RequestParam(defaultValue = "") String q,
                         @RequestParam(defaultValue = "") String estado,
                         @RequestParam(defaultValue = "") String tipo,
                         @RequestParam(defaultValue = "") String fecha,
                         @RequestParam(defaultValue = "TODOS") String buscarPor,
                         @RequestParam(defaultValue = "0") int pagina,
                         HttpSession sesion, Model model) {
        try {
            List<Map<String, String>> todas = infracciones.listar(token(sesion));
            List<Map<String, String>> fuente = todas;
            if (!q.isBlank() && !"TODOS".equals(buscarPor)) {
                fuente = switch (buscarPor) {
                    case "PLACA" -> infracciones.porPlaca(normalizarPlaca(q), token(sesion));
                    case "DPI" -> infracciones.porDpi(normalizarDpi(q), token(sesion));
                    case "NUMERO" -> infracciones.porNumero(q.trim().toUpperCase(), token(sesion));
                    default -> todas;
                };
            }
            List<Map<String, String>> filtradas = fuente.stream()
                    .filter(i -> !"TODOS".equals(buscarPor) || contiene(i, q))
                    .filter(i -> estado.isBlank() || estado.equalsIgnoreCase(i.getOrDefault("estado", "")))
                    .filter(i -> tipo.isBlank() || tipo.equalsIgnoreCase(i.getOrDefault("tipoInfraccion", "")))
                    .filter(i -> fecha.isBlank() || fecha.equals(i.getOrDefault("fechaInfraccion", "")))
                    .sorted(Comparator.comparing((Map<String, String> i) -> i.getOrDefault("fechaInfraccion", "")).reversed())
                    .toList();
            Set<String> estados = todas.stream().map(i -> i.get("estado")).filter(v -> v != null && !v.isBlank()).collect(Collectors.toCollection(TreeSet::new));
            Set<String> tipos = todas.stream().map(i -> i.get("tipoInfraccion")).filter(v -> v != null && !v.isBlank()).collect(Collectors.toCollection(TreeSet::new));
            int totalPaginas = (int) Math.ceil((double) filtradas.size() / TAMANO_PAGINA);
            int paginaSegura = Math.max(0, Math.min(pagina, Math.max(0, totalPaginas - 1)));
            int desde = paginaSegura * TAMANO_PAGINA;
            model.addAttribute("infracciones", filtradas.subList(Math.min(desde, filtradas.size()), Math.min(desde + TAMANO_PAGINA, filtradas.size())));
            model.addAttribute("total", filtradas.size());
            model.addAttribute("totalPendientes", todas.stream().filter(i -> "Pendiente".equalsIgnoreCase(i.get("estado"))).count());
            model.addAttribute("totalPagadas", todas.stream().filter(i -> "Pagada".equalsIgnoreCase(i.get("estado"))).count());
            model.addAttribute("totalPaginas", totalPaginas);
            model.addAttribute("pagina", paginaSegura);
            model.addAttribute("tamano", TAMANO_PAGINA);
            model.addAttribute("estados", estados);
            model.addAttribute("tipos", tipos);
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            valoresVacios(model);
        } catch (SoapFaultException e) {
            model.addAttribute("error", e.getMessage());
            valoresVacios(model);
        } catch (RuntimeException e) {
            model.addAttribute("error", "No se pudo cargar el listado de infracciones.");
            valoresVacios(model);
        }
        model.addAttribute("q", q);
        model.addAttribute("estado", estado);
        model.addAttribute("tipo", tipo);
        model.addAttribute("fecha", fecha);
        model.addAttribute("buscarPor", buscarPor);
        return "admin-infracciones";
    }

    @GetMapping("/admin/infracciones/{numero}")
    public String detalle(@PathVariable String numero, HttpSession sesion, Model model) {
        try {
            List<Map<String, String>> filas = infracciones.porNumero(numero, token(sesion));
            model.addAttribute("infraccion", filas.isEmpty() ? Map.of() : filas.get(0));
        } catch (SoapFaultException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("infraccion", Map.of());
        } catch (RuntimeException e) {
            model.addAttribute("error", "No se pudo consultar la infracción.");
            model.addAttribute("infraccion", Map.of());
        }
        return "admin-infraccion-detalle";
    }

    @PostMapping("/admin/infracciones/{numero}/anular")
    public String anular(@PathVariable String numero, @RequestParam(name = "_csrf", defaultValue = "") String csrf,
                         HttpSession sesion, RedirectAttributes redirect) {
        if (!csrfValido(sesion, csrf)) {
            redirect.addFlashAttribute("error", "La sesión del formulario venció. Inténtalo de nuevo.");
            return "redirect:/admin/infracciones";
        }
        try {
            infracciones.anular(numero, token(sesion));
            redirect.addFlashAttribute("exito", "La infracción " + numero + " fue anulada.");
        } catch (SoapFaultException e) {
            redirect.addFlashAttribute("error", mensajeAnulacion(e.getMessage()));
        } catch (RuntimeException e) {
            redirect.addFlashAttribute("error", "No se pudo anular la infracción.");
        }
        return "redirect:/admin/infracciones";
    }

    private String mensajeAnulacion(String mensajeApi) {
        if (mensajeApi != null && mensajeApi.toLowerCase(java.util.Locale.ROOT).contains("pagad")) {
            return "No se puede anular esta infracción porque ya está pagada.";
        }
        return mensajeApi == null || mensajeApi.isBlank()
                ? "No se pudo anular la infracción."
                : mensajeApi;
    }

    private boolean contiene(Map<String, String> infraccion, String query) {
        if (query == null || query.isBlank()) return true;
        String q = query.trim().toLowerCase();
        return List.of("numeroInfraccion", "placaVehiculo", "tipoInfraccion", "dpiPersona", "nombrePersona")
                .stream().map(infraccion::get).filter(v -> v != null).anyMatch(v -> v.toLowerCase().contains(q));
    }

    private String normalizarPlaca(String valor) {
        String placa = valor.replaceAll("\\s+", "").toUpperCase();
        if (!placa.matches("^[PMCA]\\d{3}[A-Z]{3}$")) throw new IllegalArgumentException("La placa debe tener formato P123ABC.");
        return placa;
    }

    private String normalizarDpi(String valor) {
        String dpi = valor.replaceAll("\\D", "");
        if (!dpi.matches("^\\d{13}$")) throw new IllegalArgumentException("El DPI debe contener 13 dígitos.");
        return dpi;
    }

    private void valoresVacios(Model model) {
        model.addAttribute("infracciones", List.of());
        model.addAttribute("total", 0);
        model.addAttribute("totalPendientes", 0);
        model.addAttribute("totalPagadas", 0);
        model.addAttribute("totalPaginas", 0);
        model.addAttribute("pagina", 0);
        model.addAttribute("tamano", TAMANO_PAGINA);
        model.addAttribute("estados", List.of());
        model.addAttribute("tipos", List.of());
    }

    private String token(HttpSession sesion) { return ((SesionUsuario) sesion.getAttribute(SesionAtributos.USUARIO)).getToken(); }

    private boolean csrfValido(HttpSession sesion, String recibido) {
        Object esperado = sesion.getAttribute(SesionAtributos.CSRF);
        return esperado instanceof String valor && MessageDigest.isEqual(valor.getBytes(StandardCharsets.UTF_8), recibido.getBytes(StandardCharsets.UTF_8));
    }
}
