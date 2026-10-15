package com.infracciones.apiinfraccionesvehiculares.Model;
import java.math.BigDecimal;

public class ReglaDescuento {
    private Integer idRegla;
    private BigDecimal porcentaje;
    private Integer diasLimite;
    private Integer estado;

    public ReglaDescuento() {}

    public ReglaDescuento(Integer idRegla, BigDecimal porcentaje, Integer diasLimite, Integer estado) {
        this.idRegla = idRegla;
        this.porcentaje = porcentaje;
        this.diasLimite = diasLimite;
        this.estado = estado;
    }

    // getters y setters
    public Integer getIdRegla() { return idRegla; }
    public void setIdRegla(Integer idRegla) { this.idRegla = idRegla; }
    public BigDecimal getPorcentaje() { return porcentaje; }
    public void setPorcentaje(BigDecimal porcentaje) { this.porcentaje = porcentaje; }
    public Integer getDiasLimite() { return diasLimite; }
    public void setDiasLimite(Integer diasLimite) { this.diasLimite = diasLimite; }
    public Integer getEstado() { return estado; }
    public void setEstado(Integer estado) { this.estado = estado; }
}
