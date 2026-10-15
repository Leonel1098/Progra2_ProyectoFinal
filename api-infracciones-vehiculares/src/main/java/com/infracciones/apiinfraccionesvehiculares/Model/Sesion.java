package com.infracciones.apiinfraccionesvehiculares.Model;

import java.time.Instant;

public class Sesion {
    private final String token;
    private final String usuario;
    private final String nombreRol;
    private final String dpiPersona;
    private final Instant expiraEn;

    public Sesion(String token, String usuario, String nombreRol, String dpiPersona, Instant expiraEn) {
        this.token = token;
        this.usuario = usuario;
        this.nombreRol = nombreRol;
        this.dpiPersona = dpiPersona;
        this.expiraEn = expiraEn;
    }

    public String getToken() { return token; }
    public String getUsuario() { return usuario; }
    public String getNombreRol() { return nombreRol; }
    public String getDpiPersona() { return dpiPersona; }
    public Instant getExpiraEn() { return expiraEn; }

    public boolean estaExpirada() {
        return Instant.now().isAfter(expiraEn);
    }
}