package com.infracciones.web.dto.response;

public record InfraccionResponse(
        String idInfraccion,
        String numeroInfraccion,
        String fechaInfraccion,
        String direccionInfraccion,
        String tipoInfraccion,
        String montoOriginal,
        String estado,
        String placaVehiculo,
        String dpiPersona,
        String nombrePersona,
        String usuarioAgente) {
}