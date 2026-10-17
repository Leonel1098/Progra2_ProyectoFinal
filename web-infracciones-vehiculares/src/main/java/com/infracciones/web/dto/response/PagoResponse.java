package com.infracciones.web.dto.response;

public record PagoResponse(
        String idPago,
        String numeroInfraccion,
        String montoInicial,
        String porcentajeDescuento,
        String montoDescuento,
        String montoPagado,
        String fechaPago) {
}
