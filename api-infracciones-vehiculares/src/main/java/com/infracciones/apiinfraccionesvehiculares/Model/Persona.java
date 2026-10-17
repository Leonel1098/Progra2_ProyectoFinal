package com.infracciones.apiinfraccionesvehiculares.Model;

public class Persona {
    private Integer idPersona;
    private String dpiPersona;
    private String nombrePersona;
    private String apellidoPersona;
    private String telefonoPersona;
    private String direccionPersona;

    public Persona() {
    }

    public Persona(Integer idPersona, String dpiPersona, String nombrePersona, String apellidoPersona, String telefonoPersona, String direccionPersona) {
        this.idPersona = idPersona;
        this.dpiPersona = dpiPersona;
        this.nombrePersona = nombrePersona;
        this.apellidoPersona = apellidoPersona;
        this.telefonoPersona = telefonoPersona;
        this.direccionPersona = direccionPersona;
    }

    public Integer getIdPersona() {
        return idPersona;
    }

    public void setIdPersona(Integer idPersona) {
        this.idPersona = idPersona;
    }

    public String getDpiPersona() {
        return dpiPersona;
    }

    public void setDpiPersona(String dpiPersona) {
        this.dpiPersona = dpiPersona;
    }

    public String getNombrePersona() {
        return nombrePersona;
    }

    public void setNombrePersona(String nombrePersona) {
        this.nombrePersona = nombrePersona;
    }

    public String getApellidoPersona() {
        return apellidoPersona;
    }

    public void setApellidoPersona(String apellidoPersona) {
        this.apellidoPersona = apellidoPersona;
    }

    public String getTelefonoPersona() {
        return telefonoPersona;
    }

    public void setTelefonoPersona(String telefonoPersona) {
        this.telefonoPersona = telefonoPersona;
    }

    public String getDireccionPersona() {
        return direccionPersona;
    }

    public void setDireccionPersona(String direccionPersona) {
        this.direccionPersona = direccionPersona;
    }
}
