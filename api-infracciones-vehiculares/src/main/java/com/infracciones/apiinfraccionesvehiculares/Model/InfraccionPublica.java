package com.infracciones.apiinfraccionesvehiculares.Model;

import java.math.BigDecimal;

public class InfraccionPublica {
    private String numeroInfraccion;
    private String fechaInfraccion;
    private String tipoInfraccion;
    private BigDecimal montoOriginal;
    private String estado;

    public InfraccionPublica() {}

    public static InfraccionPublica desde(InfraccionDetalle d) {
        InfraccionPublica p = new InfraccionPublica();
        p.numeroInfraccion = d.getNumeroInfraccion();
        p.fechaInfraccion = d.getFechaInfraccion();
        p.tipoInfraccion = d.getTipoInfraccion();
        p.montoOriginal = d.getMontoOriginal();
        p.estado = d.getEstado();
        return p;
    }

    public String getNumeroInfraccion() { return numeroInfraccion; }
    public void setNumeroInfraccion(String numeroInfraccion) { this.numeroInfraccion = numeroInfraccion; }

    public String getFechaInfraccion() { return fechaInfraccion; }
    public void setFechaInfraccion(String fechaInfraccion) { this.fechaInfraccion = fechaInfraccion; }

    public String getTipoInfraccion() { return tipoInfraccion; }
    public void setTipoInfraccion(String tipoInfraccion) { this.tipoInfraccion = tipoInfraccion; }

    public BigDecimal getMontoOriginal() { return montoOriginal; }
    public void setMontoOriginal(BigDecimal montoOriginal) { this.montoOriginal = montoOriginal; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}