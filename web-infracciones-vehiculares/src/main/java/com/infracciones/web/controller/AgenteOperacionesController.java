package com.infracciones.web.controller;

import com.infracciones.web.config.SesionAtributos;
import com.infracciones.web.dto.response.SesionUsuario;
import com.infracciones.web.service.AgenteOperacionesService;
import com.infracciones.web.service.SoapFaultException;
import com.infracciones.web.service.ServicioInfraccionesException;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Controller
public class AgenteOperacionesController {

    private static final Pattern DPI = Pattern.compile("^\\d{13}$");
    private static final Pattern PLACA = Pattern.compile("^[PMCA]\\d{3}[A-Z]{3}$");
    private final AgenteOperacionesService operaciones;

    public AgenteOperacionesController(AgenteOperacionesService operaciones) {
        this.operaciones = operaciones;
    }

    @GetMapping("/agente/operaciones")
    public String inicio(HttpSession sesion, Model model) {
        preparar(sesion, model);
        return "agente-operaciones";
    }

    @PostMapping("/agente/operaciones/personas/listar")
    public String listarPersonas(@RequestParam(name = "_csrf") String csrf, HttpSession sesion, Model model) {
        return ejecutar(sesion, model, csrf, "Personas", () -> operaciones.listarPersonas(token(sesion)));
    }

    @PostMapping("/agente/operaciones/personas/consultar")
    public String consultarPersona(@RequestParam String dpi, @RequestParam(name = "_csrf") String csrf,
                                   HttpSession sesion, Model model) {
        String valor = limpiarDpi(dpi);
        if (!DPI.matcher(valor).matches()) return error(sesion, model, "Ingresa un DPI válido de 13 dígitos.");
        model.addAttribute("dpiConsultaSeleccionado", valor);
        return ejecutar(sesion, model, csrf, "Persona por DPI", () -> operaciones.consultarPersona(valor, token(sesion)));
    }

    @PostMapping("/agente/operaciones/personas/crear")
    public String crearPersona(@RequestParam String dpi, @RequestParam String nombre,
                               @RequestParam String apellido, @RequestParam(defaultValue = "") String telefono,
                               @RequestParam(defaultValue = "") String direccion, @RequestParam(name = "_csrf") String csrf,
                               HttpSession sesion, Model model) {
        String valor = limpiarDpi(dpi);
        if (!DPI.matcher(valor).matches()) return error(sesion, model, "Ingresa un DPI válido de 13 dígitos.");
        if (nombre.isBlank() || apellido.isBlank()) return error(sesion, model, "Nombre y apellido son obligatorios.");
        return ejecutar(sesion, model, csrf, "Persona creada", () -> operaciones.crearPersona(
                valor, nombre.trim(), apellido.trim(), telefono.trim(), direccion.trim(), token(sesion)));
    }

    @PostMapping("/agente/operaciones/vehiculos/listar")
    public String listarVehiculos(@RequestParam(name = "_csrf") String csrf, HttpSession sesion, Model model) {
        return ejecutar(sesion, model, csrf, "Vehículos", () -> operaciones.listarVehiculos(token(sesion)));
    }

    @PostMapping("/agente/operaciones/vehiculos/consultar")
    public String consultarVehiculo(@RequestParam String placa, @RequestParam(name = "_csrf") String csrf,
                                    HttpSession sesion, Model model) {
        String valor = placa.trim().replaceAll("\\s+", "").toUpperCase();
        if (!PLACA.matcher(valor).matches()) return error(sesion, model, "La placa debe tener formato P123ABC, M123ABC, C123ABC o A123ABC.");
        model.addAttribute("placaConsultaSeleccionada", valor);
        return ejecutar(sesion, model, csrf, "Vehículo por placa", () -> operaciones.consultarVehiculo(valor, token(sesion)));
    }

    @PostMapping("/agente/operaciones/vehiculos/crear")
    public String crearVehiculo(@RequestParam String placa, @RequestParam String marca,
                                @RequestParam String modeloVehiculo, @RequestParam String color,
                                @RequestParam(name = "_csrf") String csrf, HttpSession sesion, Model model) {
        String valor = placa.trim().replaceAll("\\s+", "").toUpperCase();
        if (!PLACA.matcher(valor).matches()) return error(sesion, model, "La placa debe tener formato P123ABC, M123ABC, C123ABC o A123ABC.");
        if (marca.isBlank() || modeloVehiculo.isBlank() || color.isBlank()) return error(sesion, model, "Completa marca, modelo y color.");
        return ejecutar(sesion, model, csrf, "Vehículo creado", () -> operaciones.crearVehiculo(
                valor, marca.trim(), modeloVehiculo.trim(), color.trim(), token(sesion)));
    }

    @PostMapping("/agente/operaciones/infracciones/numero")
    public String porNumero(@RequestParam String numero, @RequestParam(name = "_csrf") String csrf,
                            HttpSession sesion, Model model) {
        if (numero.isBlank()) return error(sesion, model, "Ingresa el número de infracción.");
        model.addAttribute("numeroSeleccionado", numero.trim());
        return ejecutar(sesion, model, csrf, "Infracción por número", () ->
                operaciones.consultarInfraccionPorNumero(numero.trim(), token(sesion)));
    }

    @PostMapping("/agente/operaciones/infracciones/placa")
    public String infraccionesPorPlaca(@RequestParam String placa, @RequestParam(name = "_csrf") String csrf,
                                       HttpSession sesion, Model model) {
        String valor = placa.trim().replaceAll("\\s+", "").toUpperCase();
        if (!PLACA.matcher(valor).matches()) return error(sesion, model, "Ingresa una placa válida (por ejemplo P123ABC).");
        model.addAttribute("placaSeleccionada", valor);
        return ejecutar(sesion, model, csrf, "Infracciones por placa", () ->
                operaciones.consultarInfraccionesPorPlaca(valor, token(sesion)));
    }

    @PostMapping("/agente/operaciones/infracciones/dpi")
    public String infraccionesPorDpi(@RequestParam String dpi, @RequestParam(name = "_csrf") String csrf,
                                     HttpSession sesion, Model model) {
        String valor = limpiarDpi(dpi);
        if (!DPI.matcher(valor).matches()) return error(sesion, model, "Ingresa un DPI válido de 13 dígitos.");
        model.addAttribute("dpiSeleccionado", valor);
        return ejecutar(sesion, model, csrf, "Infracciones por DPI", () ->
                operaciones.consultarInfraccionesPorDpi(valor, token(sesion)));
    }

    private String ejecutar(HttpSession sesion, Model model, String csrf, String titulo,
                            Consulta consulta) {
        preparar(sesion, model);
        if (!csrfValido(sesion, csrf)) {
            model.addAttribute("error", "El formulario venció. Actualiza la página e inténtalo de nuevo.");
            return "agente-operaciones";
        }
        try {
            List<Map<String, String>> filas = consulta.obtener();
            model.addAttribute("tituloResultados", titulo);
            model.addAttribute("filas", filas);
            List<String> columnas = titulo.toLowerCase().contains("persona")
                    ? operaciones.columnasPersonas(filas) : operaciones.columnas(filas);
            model.addAttribute("columnas", columnas);
            model.addAttribute("etiquetas", operaciones.etiquetas(columnas));
            model.addAttribute("sinResultados", filas.isEmpty());
        } catch (SoapFaultException e) {
            model.addAttribute("error", e.getMessage());
        } catch (ServicioInfraccionesException e) {
            model.addAttribute("error", "No fue posible comunicarse con el API de infracciones.");
        }
        return "agente-operaciones";
    }

    private String error(HttpSession sesion, Model model, String mensaje) {
        preparar(sesion, model);
        model.addAttribute("error", mensaje);
        return "agente-operaciones";
    }

    private void preparar(HttpSession sesion, Model model) {
        model.addAttribute("agente", sesion.getAttribute(SesionAtributos.USUARIO));
        String token = token(sesion);
        try {
            model.addAttribute("personasDisponibles", operaciones.listarPersonas(token));
        } catch (RuntimeException e) {
            model.addAttribute("personasDisponibles", List.of());
            model.addAttribute("errorPersonasDisponibles", "No se pudo cargar el listado de personas.");
        }
        try {
            model.addAttribute("vehiculosDisponibles", operaciones.listarVehiculos(token));
        } catch (RuntimeException e) {
            model.addAttribute("vehiculosDisponibles", List.of());
            model.addAttribute("errorVehiculosDisponibles", "No se pudo cargar el listado de vehículos.");
        }
        try {
            model.addAttribute("numerosDisponibles", operaciones.listarNumerosInfraccion(token));
        } catch (RuntimeException e) {
            model.addAttribute("numerosDisponibles", List.of());
            model.addAttribute("errorNumerosDisponibles", "No se pudo cargar el listado de números de infracción.");
        }
    }

    private String token(HttpSession sesion) {
        return ((SesionUsuario) sesion.getAttribute(SesionAtributos.USUARIO)).getToken();
    }

    private String limpiarDpi(String dpi) {
        return dpi.replaceAll("\\D", "");
    }

    private boolean csrfValido(HttpSession sesion, String recibido) {
        Object esperado = sesion.getAttribute(SesionAtributos.CSRF);
        return esperado instanceof String valor && MessageDigest.isEqual(
                valor.getBytes(StandardCharsets.UTF_8), recibido.getBytes(StandardCharsets.UTF_8));
    }

    @FunctionalInterface
    private interface Consulta { List<Map<String, String>> obtener(); }
}
