package com.infracciones.web.controller;

import com.infracciones.web.config.SesionAtributos;
import com.infracciones.web.dto.response.SesionUsuario;
import com.infracciones.web.service.AgenteInfraccionService;
import com.infracciones.web.service.AgenteOperacionesService;
import com.infracciones.web.service.ServicioInfraccionesException;
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
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

@Controller
public class AgenteController {

    private static final Pattern PLACA = Pattern.compile("^[PMCA]\\d{3}[A-Z]{3}$");
    private static final Pattern DPI = Pattern.compile("^\\d{13}$");

    private final AgenteInfraccionService infracciones;
    private final AgenteOperacionesService operaciones;

    public AgenteController(AgenteInfraccionService infracciones, AgenteOperacionesService operaciones) {
        this.infracciones = infracciones;
        this.operaciones = operaciones;
    }

    @GetMapping("/agente/infracciones/nueva")
    public String nueva(HttpSession sesion, Model model) {
        prepararVista(sesion, model);
        return "agente-infraccion";
    }

    @PostMapping("/agente/infracciones/vehiculo")
    public String crearVehiculo(@RequestParam String placaNueva, @RequestParam String marcaNueva,
                                @RequestParam String modeloNuevo, @RequestParam String colorNuevo,
                                @RequestParam(name = "_csrf", defaultValue = "") String csrf,
                                @RequestParam(defaultValue = "") String dpiConductor,
                                @RequestParam(defaultValue = "") String fecha,
                                @RequestParam(defaultValue = "") String direccion,
                                @RequestParam(defaultValue = "") String codigoInfraccion,
                                HttpSession sesion, RedirectAttributes redirect) {
        SesionUsuario agente = (SesionUsuario) sesion.getAttribute(SesionAtributos.USUARIO);
        String placa = placaNueva.replaceAll("[^A-Za-z0-9]", "").toUpperCase();
        if (!csrfValido(sesion, csrf)) return errorAlta(redirect, "El formulario venció. Actualiza la página.");
        if (!PLACA.matcher(placa).matches() || marcaNueva.isBlank() || modeloNuevo.isBlank() || colorNuevo.isBlank()) {
            return errorAlta(redirect, "Completa una placa válida, marca, modelo y color.");
        }
        try {
            operaciones.crearVehiculo(placa, marcaNueva.trim(), modeloNuevo.trim(), colorNuevo.trim(), agente.getToken());
            conservarFormulario(redirect, placa, dpiConductor, fecha, direccion, codigoInfraccion);
            redirect.addFlashAttribute("mensajeAlta", "Vehículo " + placa + " creado. Ya está disponible en el listado.");
        } catch (SoapFaultException e) {
            return errorAlta(redirect, e.getMessage());
        } catch (ServicioInfraccionesException e) {
            return errorAlta(redirect, "No fue posible crear el vehículo. Inténtalo de nuevo.");
        }
        return "redirect:/agente/infracciones/nueva";
    }

    @PostMapping("/agente/infracciones/persona")
    public String crearPersona(@RequestParam String dpiNuevo, @RequestParam String nombreNuevo,
                               @RequestParam String apellidoNuevo, @RequestParam(defaultValue = "") String telefonoNuevo,
                               @RequestParam(defaultValue = "") String direccionNueva,
                               @RequestParam(name = "_csrf", defaultValue = "") String csrf,
                               @RequestParam(defaultValue = "") String placa,
                               @RequestParam(defaultValue = "") String fecha,
                               @RequestParam(defaultValue = "") String direccion,
                               @RequestParam(defaultValue = "") String codigoInfraccion,
                               HttpSession sesion, RedirectAttributes redirect) {
        SesionUsuario agente = (SesionUsuario) sesion.getAttribute(SesionAtributos.USUARIO);
        String dpi = dpiNuevo.replaceAll("\\D", "");
        if (!csrfValido(sesion, csrf)) return errorAlta(redirect, "El formulario venció. Actualiza la página.");
        if (!DPI.matcher(dpi).matches() || nombreNuevo.isBlank() || apellidoNuevo.isBlank()) {
            return errorAlta(redirect, "Completa un DPI válido, nombre y apellido.");
        }
        try {
            operaciones.crearPersona(dpi, nombreNuevo.trim(), apellidoNuevo.trim(), telefonoNuevo.trim(),
                    direccionNueva.trim(), agente.getToken());
            conservarFormulario(redirect, placa, dpi, fecha, direccion, codigoInfraccion);
            redirect.addFlashAttribute("mensajeAlta", "Persona con DPI " + dpi + " creada. Ya está disponible en el listado.");
        } catch (SoapFaultException e) {
            return errorAlta(redirect, e.getMessage());
        } catch (ServicioInfraccionesException e) {
            return errorAlta(redirect, "No fue posible crear la persona. Inténtalo de nuevo.");
        }
        return "redirect:/agente/infracciones/nueva";
    }

    private String errorAlta(RedirectAttributes redirect, String mensaje) {
        redirect.addFlashAttribute("errorAlta", mensaje);
        return "redirect:/agente/infracciones/nueva";
    }

    private void conservarFormulario(RedirectAttributes redirect, String placa, String dpi,
                                     String fecha, String direccion, String codigo) {
        redirect.addFlashAttribute("placa", placa);
        redirect.addFlashAttribute("dpiConductor", dpi);
        redirect.addFlashAttribute("fecha", fecha);
        redirect.addFlashAttribute("direccion", direccion);
        redirect.addFlashAttribute("codigoInfraccion", codigo);
    }

    @PostMapping("/agente/infracciones")
    public String registrar(@RequestParam(defaultValue = "") String placa,
                            @RequestParam(defaultValue = "") String dpiConductor,
                            @RequestParam(defaultValue = "") String fecha,
                            @RequestParam(defaultValue = "") String direccion,
                            @RequestParam(defaultValue = "") String codigoInfraccion,
                            @RequestParam(name = "_csrf", defaultValue = "") String csrf,
                            HttpSession sesion, Model model, RedirectAttributes redirect) {
        SesionUsuario agente = (SesionUsuario) sesion.getAttribute(SesionAtributos.USUARIO);
        prepararVista(sesion, model);
        model.addAttribute("placa", placa);
        model.addAttribute("dpiConductor", dpiConductor);
        model.addAttribute("fecha", fecha);
        model.addAttribute("direccion", direccion);
        model.addAttribute("codigoInfraccion", codigoInfraccion);

        if (!csrfValido(sesion, csrf)) {
            model.addAttribute("error", "El formulario venció. Actualiza la página e inténtalo de nuevo.");
            return "agente-infraccion";
        }

        String placaNormalizada = placa.replaceAll("[^A-Za-z0-9]", "").toUpperCase();
        String dpiNormalizado = dpiConductor.replaceAll("\\D", "");
        if (!PLACA.matcher(placaNormalizada).matches()) {
            model.addAttribute("error", "Selecciona una placa válida del listado.");
            return "agente-infraccion";
        }
        if (listaPlacas(model).stream().noneMatch(v -> placaNormalizada.equals(v.get("placa")))) {
            model.addAttribute("error", "La placa seleccionada ya no está disponible. Actualiza la página.");
            return "agente-infraccion";
        }
        if (!DPI.matcher(dpiNormalizado).matches()) {
            model.addAttribute("error", "Selecciona un DPI válido del listado.");
            return "agente-infraccion";
        }
        if (listaPersonas(model).stream().noneMatch(p -> dpiNormalizado.equals(p.get("dpi")))) {
            model.addAttribute("error", "El DPI seleccionado ya no está disponible. Actualiza la página.");
            return "agente-infraccion";
        }
        if (direccion.isBlank() || direccion.trim().length() > 200) {
            model.addAttribute("error", "Ingresa una dirección de hasta 200 caracteres.");
            return "agente-infraccion";
        }
        if (codigoInfraccion.isBlank() || modelCatalogo(model).stream()
                .noneMatch(tipo -> codigoInfraccion.equals(tipo.get("codigo")))) {
            model.addAttribute("error", "Selecciona un tipo de infracción válido.");
            return "agente-infraccion";
        }

        String fechaNormalizada = fecha.isBlank() ? LocalDate.now().toString() : fecha;
        try {
            LocalDate.parse(fechaNormalizada);
        } catch (RuntimeException e) {
            model.addAttribute("error", "La fecha no tiene un formato válido.");
            return "agente-infraccion";
        }
        if (!fechaNormalizada.equals(LocalDate.now().toString())) {
            model.addAttribute("error", "La fecha de la infracción debe ser el día actual.");
            return "agente-infraccion";
        }

        try {
            String numero = "INFR-" + UUID.randomUUID().toString()
                    .replace("-", "").substring(0, 10).toUpperCase();
            Map<String, String> boleta = infracciones.registrar(numero, fechaNormalizada,
                    direccion.trim(), dpiNormalizado, placaNormalizada, agente.getUsuario(),
                    codigoInfraccion, agente.getToken());
            redirect.addFlashAttribute("boleta", boleta);
            return "redirect:/agente/infracciones/nueva";
        } catch (SoapFaultException e) {
            model.addAttribute("error", e.getMessage());
        } catch (ServicioInfraccionesException e) {
            model.addAttribute("error", "No se pudo registrar la infracción. Inténtalo más tarde.");
        }
        return "agente-infraccion";
    }

    private void prepararVista(HttpSession sesion, Model model) {
        SesionUsuario agente = (SesionUsuario) sesion.getAttribute(SesionAtributos.USUARIO);
        model.addAttribute("agente", agente);
        String hoy = LocalDate.now().toString();
        model.addAttribute("fechaHoy", hoy);
        model.asMap().putIfAbsent("fecha", hoy);
        try {
            model.addAttribute("catalogo", infracciones.listarCatalogo(agente.getToken()));
        } catch (RuntimeException e) {
            model.addAttribute("catalogo", List.of());
            model.addAttribute("errorCatalogo", "No se pudo cargar el catálogo de infracciones.");
        }
        try {
            model.addAttribute("vehiculos", infracciones.listarVehiculos(agente.getToken()));
        } catch (RuntimeException e) {
            model.addAttribute("vehiculos", List.of());
            model.addAttribute("errorVehiculos", "No se pudo cargar el listado de vehículos.");
        }
        try {
            model.addAttribute("personas", infracciones.listarPersonas(agente.getToken()));
        } catch (RuntimeException e) {
            model.addAttribute("personas", List.of());
            model.addAttribute("errorPersonas", "No se pudo cargar el listado de personas.");
        }
    }

    private List<Map<String, String>> listaPlacas(Model model) {
        Object vehiculos = model.asMap().get("vehiculos");
        return vehiculos instanceof List<?> lista ? lista.stream().filter(Map.class::isInstance)
                .map(elemento -> (Map<String, String>) elemento).toList() : List.of();
    }

    private List<Map<String, String>> listaPersonas(Model model) {
        Object personas = model.asMap().get("personas");
        return personas instanceof List<?> lista ? lista.stream().filter(Map.class::isInstance)
                .map(elemento -> (Map<String, String>) elemento).toList() : List.of();
    }

    private List<Map<String, String>> modelCatalogo(Model model) {
        Object catalogo = model.asMap().get("catalogo");
        if (catalogo instanceof List<?> lista) {
            return lista.stream().filter(Map.class::isInstance)
                    .map(elemento -> (Map<String, String>) elemento).toList();
        }
        return List.of();
    }

    private boolean csrfValido(HttpSession sesion, String recibido) {
        Object esperado = sesion.getAttribute(SesionAtributos.CSRF);
        return esperado instanceof String valor && MessageDigest.isEqual(
                valor.getBytes(StandardCharsets.UTF_8), recibido.getBytes(StandardCharsets.UTF_8));
    }
}
