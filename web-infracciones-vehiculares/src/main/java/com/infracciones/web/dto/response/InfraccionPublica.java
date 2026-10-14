package com.infracciones.web.dto.response;

public class InfraccionPublica {

    private final String numeroBoleta;
    private final String fecha;
    private final String motivo;
    private final String monto;
    private final String estado;

    public InfraccionPublica(String numeroBoleta, String fecha, String motivo,
                             String monto, String estado) {
        this.numeroBoleta = numeroBoleta;
        this.fecha = fecha;
        this.motivo = motivo;
        this.monto = monto;
        this.estado = estado;
    }

    public static InfraccionPublica desde(InfraccionResponse completa) {
        return new InfraccionPublica(
                completa.numeroInfraccion(),
                completa.fechaInfraccion(),
                completa.tipoInfraccion(),
                completa.montoOriginal(),
                completa.estado());
    }

    public String getNumeroBoleta() { return numeroBoleta; }
    public String getFecha() { return fecha; }
    public String getMotivo() { return motivo; }
    public String getMonto() { return monto; }
    public String getEstado() { return estado; }

    public boolean isPendiente() {
        return "Pendiente".equalsIgnoreCase(estado);
    }
}