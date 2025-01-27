package com.model;

import java.io.Serializable;


import jakarta.persistence.*;

@Entity
@Table(name = "usuarios")
public class Usuario implements Serializable {
	
	private static final long serialVersionUID = 1L; // Requerido para serialización

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String correo;
    private String contras;

    public Usuario() {}

    public Usuario(String nombre, String correo, String contras) {
        this.nombre = nombre;
        this.correo = correo;
        this.contras = contras;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    
    public String getContras() { return contras; }
    public void setContras(String contras) { this.contras = contras; }
}
