package com.infracciones.web.controller;

import com.infracciones.web.config.SesionAtributos;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class InicioController {

    @GetMapping("/inicio")
    public String inicio(HttpSession sesion, Model model) {
        model.addAttribute("infraccionPorPagar",
                sesion.getAttribute(SesionAtributos.INFRACCION_POR_PAGAR));
        return "inicio";
    }
}