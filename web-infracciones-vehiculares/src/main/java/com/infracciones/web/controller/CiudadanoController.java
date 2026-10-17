package com.infracciones.web.controller;

import com.infracciones.web.config.SesionAtributos;
import com.infracciones.web.dto.response.InfraccionResponse;
import com.infracciones.web.dto.response.InfraccionPublica;
import com.infracciones.web.dto.response.PagoResponse;
import com.infracciones.web.dto.response.SesionUsuario;
import com.infracciones.web.service.AccesoDenegadoException;
import com.infracciones.web.service.ConsultaInfraccionService;
import com.infracciones.web.service.PagoClienteService;
import com.infracciones.web.service.ServicioInfraccionesException;
import com.infracciones.web.service.SesionInvalidaException;
import com.infracciones.web.service.SoapFaultException;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;
import java.util.regex.Pattern;

@Controller
public class CiudadanoController {

    private static final Pattern NUMERO = Pattern.compile("^[A-Z0-9-]{3,30}$");
    private static final Pattern PLACA = Pattern.compile("^[PMCA]\\d{3}[A-Z]{3}$");

    private final ConsultaInfraccionService consultas;
    private final PagoClienteService pagos;

    public CiudadanoController(ConsultaInfraccionService consultas, PagoClienteService pagos) {
        this.consultas = consultas;
        this.pagos = pagos;
    }

    @GetMapping("/ciudadano/infracciones")
    public String misInfracciones(HttpSession sesion, Model model) {
        SesionUsuario usuario = usuario(sesion);
        model.addAttribute("tipoBusqueda", "PLACA");
        model.addAttribute("tipoPlaca", "P");
        cargarMisInfracciones(usuario, model);
        return "mis-infracciones";
    }

    @PostMapping("/ciudadano/infracciones/consulta")
    public String consultarMisInfracciones(@RequestParam(defaultValue = "PLACA") String tipoBusqueda,
                                           @RequestParam(defaultValue = "P") String tipoPlaca,
                                           @RequestParam(defaultValue = "") String valor,
                                           @RequestParam(name = "_csrf", defaultValue = "") String csrf,
                                           HttpSession sesion, Model model) {
        SesionUsuario usuario = usuario(sesion);
        cargarMisInfracciones(usuario, model);
        if (!csrfValido(sesion, csrf)) {
            model.addAttribute("mensajeBusqueda", "La sesión del formulario venció. Vuelve a intentarlo.");
            return "mis-infracciones";
        }

        boolean porPlaca = !"INFRACCION".equals(tipoBusqueda);
        String limpio = valor.trim().toUpperCase();
        String prefijo = tipoPlaca.trim().toUpperCase();
        String criterio = porPlaca
                ? prefijo + limpio.replaceAll("[^A-Z0-9]", "")
                : limpio;
        model.addAttribute("tipoBusqueda", porPlaca ? "PLACA" : "INFRACCION");
        model.addAttribute("tipoPlaca", prefijo);
        model.addAttribute("valor", limpio);

        if (!(porPlaca ? PLACA : NUMERO).matcher(criterio).matches()) {
            model.addAttribute("mensajeBusqueda", porPlaca
                    ? "La placa no tiene un formato válido. Ejemplo: P123ABC."
                    : "El número de infracción no tiene un formato válido. Ejemplo: INFR-0001.");
            return "mis-infracciones";
        }

        try {
            List<InfraccionPublica> resultado = porPlaca
                    ? consultas.consultarPorPlaca(criterio, usuario.getToken())
                    : consultas.consultarPorNumero(criterio, usuario.getToken());
            model.addAttribute("resultadosBusqueda", resultado);
            if (resultado.isEmpty()) {
                model.addAttribute("sinResultados", true);
            }
        } catch (SesionInvalidaException | AccesoDenegadoException e) {
            throw e;
        } catch (SoapFaultException e) {
            model.addAttribute("mensajeBusqueda", "No se encontró una infracción asociada a tu cuenta.");
        } catch (ServicioInfraccionesException e) {
            model.addAttribute("mensajeBusqueda", "No pudimos realizar la consulta. Inténtalo más tarde.");
        }
        return "mis-infracciones";
    }

    private void cargarMisInfracciones(SesionUsuario usuario, Model model) {
        try {
            List<InfraccionResponse> lista = consultas.consultarPorDpi(
                    usuario.getDpiPersona(), usuario.getToken());
            model.addAttribute("pendientes", lista.stream()
                    .filter(i -> "Pendiente".equalsIgnoreCase(i.estado())).toList());
            model.addAttribute("historial", lista.stream()
                    .filter(i -> !"Pendiente".equalsIgnoreCase(i.estado())).toList());
        } catch (ServicioInfraccionesException e) {
            model.addAttribute("mensaje", "No pudimos cargar tus infracciones. Inténtalo más tarde.");
        }
    }

    @GetMapping("/ciudadano/pago")
    public String confirmarPago(@RequestParam(defaultValue = "") String numero,
                                HttpSession sesion, Model model) {
        SesionUsuario usuario = usuario(sesion);
        String id = numero.trim().toUpperCase();
        if (!NUMERO.matcher(id).matches()) {
            model.addAttribute("mensaje", "El número de infracción no es válido.");
            return "pago";
        }

        try {
            model.addAttribute("pago", pagos.calcularMontoAPagar(id, usuario.getToken()));
            model.addAttribute("numero", id);
        } catch (SesionInvalidaException | AccesoDenegadoException e) {
            throw e;
        } catch (SoapFaultException e) {
            // Mensajes de negocio del backend (ya pagada, anulada, no es suya...).
            model.addAttribute("mensaje", e.getMessage());
        } catch (ServicioInfraccionesException e) {
            model.addAttribute("mensaje", "No pudimos calcular el monto. Inténtalo más tarde.");
        }
        return "pago";
    }

    @PostMapping("/ciudadano/pago")
    public String pagar(@RequestParam(defaultValue = "") String numero,
                        @RequestParam(name = "_csrf", defaultValue = "") String csrf,
                        HttpSession sesion, Model model) {
        SesionUsuario usuario = usuario(sesion);
        if (!csrfValido(sesion, csrf)) {
            return "redirect:/inicio";
        }

        String id = numero.trim().toUpperCase();
        if (!NUMERO.matcher(id).matches()) {
            model.addAttribute("mensaje", "El número de infracción no es válido.");
            return "pago";
        }

        try {
            PagoResponse comprobante = pagos.aplicarPago(id, usuario.getToken());
            sesion.removeAttribute(SesionAtributos.INFRACCION_POR_PAGAR);
            model.addAttribute("comprobante", comprobante);
        } catch (SesionInvalidaException | AccesoDenegadoException e) {
            throw e;
        } catch (SoapFaultException e) {
            model.addAttribute("mensaje", e.getMessage());
        } catch (ServicioInfraccionesException e) {
            model.addAttribute("mensaje", "No pudimos procesar el pago. Inténtalo más tarde.");
        }
        return "pago";
    }

    private SesionUsuario usuario(HttpSession sesion) {
        return (SesionUsuario) sesion.getAttribute(SesionAtributos.USUARIO);
    }

    private boolean csrfValido(HttpSession sesion, String recibido) {
        return sesion.getAttribute(SesionAtributos.CSRF) instanceof String esperado
                && MessageDigest.isEqual(
                esperado.getBytes(StandardCharsets.UTF_8),
                recibido.getBytes(StandardCharsets.UTF_8));
    }
}
