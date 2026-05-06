package com.laboratorio.streams.dto;

import com.laboratorio.streams.model.Estado;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TareaDTO {
    
    private Long id;
    
    @NotBlank(message = "El título es obligatorio y no puede estar vacío")
    @Size(min = 3, max = 100, message = "El título debe tener entre 3 y 100 caracteres")
    private String titulo;
    
    @NotBlank(message = "La descripción es obligatoria y no puede estar vacía")
    @Size(min = 10, max = 500, message = "La descripción debe tener entre 10 y 500 caracteres")
    private String descripcion;
    
    @NotNull(message = "El estado es obligatorio")
    private Estado estado;
    
    @NotNull(message = "La prioridad es obligatoria")
    @Min(value = 1, message = "La prioridad debe ser mínimo 1")
    @Max(value = 5, message = "La prioridad debe ser máximo 5")
    private Integer prioridad;
    
    private LocalDateTime fecha_creacion;
}
