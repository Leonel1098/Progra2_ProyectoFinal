package com.infracciones.apiinfraccionesvehiculares.Model;

import java.math.BigDecimal;

public class TipoInfraccion {
    private Integer idTipoInfraccion;
    private String codigo;
    private String nombre;
    private String descripcion;
    private BigDecimal monto;

    public TipoInfraccion() {}

    public Integer getIdTipoInfraccion() {
        return idTipoInfraccion;
    }

    public void setIdTipoInfraccion(Integer idTipoInfraccion) {
        this.idTipoInfraccion = idTipoInfraccion;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }
}
