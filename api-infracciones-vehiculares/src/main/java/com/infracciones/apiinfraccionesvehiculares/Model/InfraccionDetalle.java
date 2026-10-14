package com.infracciones.apiinfraccionesvehiculares.Model;

import java.math.BigDecimal;

public class InfraccionDetalle {
    private Integer idInfraccion;
    private String numeroInfraccion;
    private String fechaInfraccion;
    private String direccionInfraccion;
    private BigDecimal montoOriginal;
    private String dpiPersona;
    private String nombrePersona;
    private String placaVehiculo;
    private String tipoInfraccion;
    private String estado;
    private String usuarioAgente;

    public InfraccionDetalle() {}

    public Integer getIdInfraccion() { return idInfraccion; }
    public void setIdInfraccion(Integer idInfraccion) { this.idInfraccion = idInfraccion; }

    public String getNumeroInfraccion() { return numeroInfraccion; }
    public void setNumeroInfraccion(String numeroInfraccion) { this.numeroInfraccion = numeroInfraccion; }

    public String getFechaInfraccion() { return fechaInfraccion; }
    public void setFechaInfraccion(String fechaInfraccion) { this.fechaInfraccion = fechaInfraccion; }

    public String getDireccionInfraccion() { return direccionInfraccion; }
    public void setDireccionInfraccion(String direccionInfraccion) { this.direccionInfraccion = direccionInfraccion; }

    public BigDecimal getMontoOriginal() { return montoOriginal; }
    public void setMontoOriginal(BigDecimal montoOriginal) { this.montoOriginal = montoOriginal; }

    public String getDpiPersona() { return dpiPersona; }
    public void setDpiPersona(String dpiPersona) { this.dpiPersona = dpiPersona; }

    public String getNombrePersona() { return nombrePersona; }
    public void setNombrePersona(String nombrePersona) { this.nombrePersona = nombrePersona; }

    public String getPlacaVehiculo() { return placaVehiculo; }
    public void setPlacaVehiculo(String placaVehiculo) { this.placaVehiculo = placaVehiculo; }

    public String getTipoInfraccion() { return tipoInfraccion; }
    public void setTipoInfraccion(String tipoInfraccion) { this.tipoInfraccion = tipoInfraccion; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getUsuarioAgente() { return usuarioAgente; }
    public void setUsuarioAgente(String usuarioAgente) { this.usuarioAgente = usuarioAgente; }
}