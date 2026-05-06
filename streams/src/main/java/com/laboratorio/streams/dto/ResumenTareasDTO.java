package com.laboratorio.streams.dto;

import com.laboratorio.streams.model.Estado;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResumenTareasDTO {
    
    private Long totalTareas;
    private Map<Estado, Long> tareasPorEstado;
    private Long tareasCompletadas;
    private Long tareasPendientes;
    private Long tareasEnProgreso;
    
    public ResumenTareasDTO(Map<Estado, Long> tareasPorEstado) {
        this.tareasPorEstado = tareasPorEstado;
        this.totalTareas = tareasPorEstado.values().stream().mapToLong(Long::longValue).sum();
        this.tareasCompletadas = tareasPorEstado.getOrDefault(Estado.COMPLETADA, 0L);
        this.tareasPendientes = tareasPorEstado.getOrDefault(Estado.PENDIENTE, 0L);
        this.tareasEnProgreso = tareasPorEstado.getOrDefault(Estado.EN_PROGRESO, 0L);
    }
}
