package com.laboratorio.streams.service;

import com.laboratorio.streams.dto.ResumenTareasDTO;
import com.laboratorio.streams.dto.TareaDTO;
import com.laboratorio.streams.model.Estado;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TareaService {
    
    // CRUD Básico
    TareaDTO crearTarea(TareaDTO tareaDTO);
    TareaDTO obtenerTareaPorId(Long id);
    List<TareaDTO> obtenerTodasLasTareas();
    TareaDTO actualizarTarea(Long id, TareaDTO tareaDTO);
    void eliminarTarea(Long id);
    
    // Paginación
    Page<TareaDTO> obtenerTareasPaginadas(Pageable pageable);
    Page<TareaDTO> obtenerTareasPorEstadoPaginadas(Estado estado, Pageable pageable);
    
    // Filtros y Búsquedas
    List<TareaDTO> obtenerTareasPorEstado(Estado estado);
    List<TareaDTO> obtenerTareasPorPrioridad(Integer prioridad);
    List<TareaDTO> obtenerTareasPorPrioridadMinima(Integer prioridad);
    List<TareaDTO> buscarTareasPorTitulo(String titulo);
    
    // Consultas Especializadas
    List<TareaDTO> obtenerTareasPendientesOrdenadas();
    
    // Resumen y Estadísticas
    ResumenTareasDTO obtenerResumenTareas();
}
