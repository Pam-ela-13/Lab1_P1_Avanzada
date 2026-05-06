package com.laboratorio.streams.model.entity;

import com.laboratorio.streams.model.Estado;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "tareas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tarea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String titulo;

    @Column(nullable = false, length = 500)
    private String descripcion;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Estado estado;

    @Column(nullable = false)
    private Integer prioridad;

    @Column(nullable = false, updatable = false, name = "fecha_creacion")
    private LocalDateTime fecha_creacion;

    @PrePersist
    public void asignarFechaCreacion() {
        if (this.fecha_creacion == null) {
            this.fecha_creacion = LocalDateTime.now();
        }
        if (this.estado == null) {
            this.estado = Estado.PENDIENTE;
        }
    }
}
