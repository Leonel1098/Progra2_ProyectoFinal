package com.infracciones.web.dto.response;

import java.time.Instant;
import java.util.Arrays;

public class SesionUsuario {

    private final String usuario;
    private final String nombreRol;
    private final String nombrePersona;
    private final String dpiPersona;
    private final String token;
    private final Instant expiraEn;

    public SesionUsuario(String usuario, String nombreRol, String nombrePersona,
                         String dpiPersona, String token, Instant expiraEn) {
        this.usuario = usuario;
        this.nombreRol = nombreRol;
        this.nombrePersona = nombrePersona;
        this.dpiPersona = dpiPersona;
        this.token = token;
        this.expiraEn = expiraEn;
    }

    public String getUsuario() { return usuario; }
    public String getNombreRol() { return nombreRol; }
    public String getNombrePersona() { return nombrePersona; }
    public String getDpiPersona() { return dpiPersona; }
    public String getToken() { return token; }
    public Instant getExpiraEn() { return expiraEn; }

    public boolean haExpirado() {
        return Instant.now().isAfter(expiraEn);
    }

    public boolean tieneRol(String... roles) {
        return Arrays.asList(roles).contains(nombreRol);
    }

    @Override
    public String toString() {
        return "SesionUsuario[usuario=" + usuario + ", rol=" + nombreRol + "]";
    }
}