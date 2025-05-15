package com.DTOs;

import java.time.LocalDateTime;

public class ArchivoDescargadoDTO {
    private int id;
    private String descripcion;
    private LocalDateTime fecha;

    // Constructor
    public ArchivoDescargadoDTO(int id, String descripcion, LocalDateTime fecha) {
        this.id = id;
        this.descripcion = descripcion;
        this.fecha = fecha;
    }

    // Getters
    public int getId() { return id; }
    public String getDescripcion() { return descripcion; }
    public LocalDateTime getFecha() { return fecha; }
}

