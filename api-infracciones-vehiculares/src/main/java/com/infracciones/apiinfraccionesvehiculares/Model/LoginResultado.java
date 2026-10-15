package com.infracciones.apiinfraccionesvehiculares.Model;

public class LoginResultado {
    private String token;
    private String usuario;
    private String nombreRol;
    private String dpiPersona;
    private String nombrePersona;
    private String expiraEn;

    public LoginResultado() {
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public String getNombreRol() { return nombreRol; }
    public void setNombreRol(String nombreRol) { this.nombreRol = nombreRol; }

    public String getDpiPersona() { return dpiPersona; }
    public void setDpiPersona(String dpiPersona) { this.dpiPersona = dpiPersona; }

    public String getNombrePersona() { return nombrePersona; }
    public void setNombrePersona(String nombrePersona) { this.nombrePersona = nombrePersona; }

    public String getExpiraEn() { return expiraEn; }
    public void setExpiraEn(String expiraEn) { this.expiraEn = expiraEn; }
}