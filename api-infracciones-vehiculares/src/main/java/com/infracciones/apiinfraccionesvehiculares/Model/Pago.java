package com.infracciones.apiinfraccionesvehiculares.Model;

import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Pago {
    private Integer idPago;
    private Integer idInfraccion;
    private String numeroInfraccion;
    private BigDecimal montoInicial;
    private BigDecimal porcentajeDescuento;
    private BigDecimal montoDescuento;
    private BigDecimal montoPagado;
    private LocalDate fechaPago;

    public Pago() {
    }

    public Integer getIdPago() { return idPago; }

    public void setIdPago(Integer idPago) { this.idPago = idPago; }

    public Integer getIdInfraccion() { return idInfraccion; }

    public void setIdInfraccion(Integer idInfraccion) { this.idInfraccion = idInfraccion; }

    public String getNumeroInfraccion() { return numeroInfraccion; }

    public void setNumeroInfraccion(String numeroInfraccion) { this.numeroInfraccion = numeroInfraccion; }

    public BigDecimal getMontoInicial() { return montoInicial; }

    public void setMontoInicial(BigDecimal montoInicial) { this.montoInicial = montoInicial; }

    public BigDecimal getPorcentajeDescuento() { return porcentajeDescuento; }

    public void setPorcentajeDescuento(BigDecimal porcentajeDescuento) { this.porcentajeDescuento = porcentajeDescuento; }

    public BigDecimal getMontoDescuento() { return montoDescuento; }

    public void setMontoDescuento(BigDecimal montoDescuento) { this.montoDescuento = montoDescuento; }

    public BigDecimal getMontoPagado() { return montoPagado; }

    public void setMontoPagado(BigDecimal montoPagado) { this.montoPagado = montoPagado; }

    @XmlJavaTypeAdapter(LocalDateAdapter.class)
    public LocalDate getFechaPago() { return fechaPago; }

    public void setFechaPago(LocalDate fechaPago) { this.fechaPago = fechaPago; }
}
