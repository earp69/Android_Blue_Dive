package com.dam.bluedive.model;

public class Usuario {
    private String nombreCompleto;
    private String fecha_nac;
    private String telefono;
    private String email;

    public Usuario() {}

    public Usuario(String nombreCompleto, String fecha_nac, String telefono, String email) {
        this.nombreCompleto = nombreCompleto;
        this.fecha_nac = fecha_nac;
        this.telefono = telefono;
        this.email = email;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public String getFecha_nac() {
        return fecha_nac;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getEmail() {
        return email;
    }
}
