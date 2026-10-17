package com.infracciones.web.controller;

import com.infracciones.web.config.SesionAtributos;
import com.infracciones.web.dto.response.InfraccionPublica;
import com.infracciones.web.dto.response.SesionUsuario;
import com.infracciones.web.service.ConsultaInfraccionService;
import com.infracciones.web.service.ServicioInfraccionesException;
import com.infracciones.web.service.SoapFaultException;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.regex.Pattern;

@Controller
public class ConsultaController {

    private static final Pattern PLACA = Pattern.compile("^[PMCA]\\d{3}[A-Z]{3}$");
    private static final Pattern NUMERO = Pattern.compile("^[A-Z0-9-]{3,30}$");

    private final ConsultaInfraccionService servicio;

    public ConsultaController(ConsultaInfraccionService servicio) {
        this.servicio = servicio;
    }

    @GetMapping({"/", "/consulta"})
    public String mostrarConsulta(HttpSession sesion, Model model) {
        String destino = redireccionSiAutenticado(sesion);
        if (destino != null) {
            return destino;
        }
        model.addAttribute("tipoBusqueda", "PLACA");
        return "consulta";
    }

    @PostMapping("/consulta")
    public String consultar(@RequestParam(defaultValue = "PLACA") String tipoBusqueda,
                            @RequestParam(defaultValue = "P") String tipoPlaca,
                            @RequestParam(defaultValue = "") String valor,
                            HttpSession sesion,
                            Model model) {

        String destino = redireccionSiAutenticado(sesion);
        if (destino != null) {
            return destino;
        }

        boolean porPlaca = !"INFRACCION".equals(tipoBusqueda);
        String limpio = valor.trim().toUpperCase();
        String tipoPlacaNormalizado = tipoPlaca.trim().toUpperCase();
        model.addAttribute("tipoPlaca", tipoPlacaNormalizado);

        model.addAttribute("tipoBusqueda", porPlaca ? "PLACA" : "INFRACCION");
        model.addAttribute("valor", limpio);

        String criterio = porPlaca
                ? tipoPlacaNormalizado + limpio.replaceAll("[^A-Z0-9]", "")
                : limpio;

        if (!(porPlaca ? PLACA : NUMERO).matcher(criterio).matches()) {
            model.addAttribute("mensaje", porPlaca
                    ? "La placa no tiene un formato válido. Ejemplo: P123ABC."
                    : "El número de infracción no tiene un formato válido. Ejemplo: INFR-0001.");
            return "consulta";
        }

        try {
            List<InfraccionPublica> encontradas = porPlaca
                    ? servicio.consultaPublicaPorPlaca(criterio)
                    : servicio.consultaPublicaPorNumero(criterio);

            if (encontradas.isEmpty()) {
                if (porPlaca) {
                    model.addAttribute("solvente", true);
                } else {
                    model.addAttribute("mensaje", "No se encontró una infracción con ese número.");
                }
            } else {
                model.addAttribute("infracciones", encontradas);
            }
        } catch (SoapFaultException e) {
            model.addAttribute("mensaje", porPlaca
                    ? "No se encontró un vehículo registrado con esa placa."
                    : "No se encontró una infracción con ese número.");
        } catch (ServicioInfraccionesException e) {
            model.addAttribute("mensaje",
                    "No pudimos realizar la consulta en este momento. Inténtalo más tarde.");
        }

        return "consulta";
    }

    private String redireccionSiAutenticado(HttpSession sesion) {
        Object atributo = sesion.getAttribute(SesionAtributos.USUARIO);
        if (!(atributo instanceof SesionUsuario usuario)) {
            return null;
        }
        if (usuario.haExpirado()) {
            sesion.invalidate();
            return null;
        }
        return usuario.tieneRol("Ciudadano")
                ? "redirect:/ciudadano/infracciones"
                : "redirect:/inicio";
    }
}
