package com.laboratorio.streams.repository;

import com.laboratorio.streams.model.entity.Tarea;
import com.laboratorio.streams.model.Estado;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TareaRepository extends JpaRepository<Tarea, Long> {
    
    // Consultas por convencion: derivadas automáticamente
    List<Tarea> findByEstado(Estado estado);
    List<Tarea> findByPrioridad(Integer prioridad);
    List<Tarea> findByTituloContainingIgnoreCase(String titulo);
    
    // Paginacion
    Page<Tarea> findAll(Pageable pageable);
    Page<Tarea> findByEstado(Estado estado, Pageable pageable);
    
    // Consultas personalizadas con @Query
    @Query("SELECT t FROM Tarea t WHERE t.prioridad >= :prioridad ORDER BY t.fecha_creacion DESC")
    List<Tarea> findTareasPorPrioridadMinima(@Param("prioridad") Integer prioridad);
    
    // Consulta personalizada: Tareas por estado y prioridad
    @Query("SELECT t FROM Tarea t WHERE t.estado = :estado AND t.prioridad <= :prioridad")
    List<Tarea> findTareasPorEstadoYPrioridad(
            @Param("estado") Estado estado,
            @Param("prioridad") Integer prioridad
    );
    
    // Consulta personalizada: Tareas pendientes ordenadas por prioridad
    @Query("SELECT t FROM Tarea t WHERE t.estado = 'PENDIENTE' ORDER BY t.prioridad DESC, t.fecha_creacion ASC")
    List<Tarea> findTareasPendientesOrdenadas();
    
    // Resumen: Conteo de tareas por estado
    @Query("SELECT t.estado, COUNT(t) FROM Tarea t GROUP BY t.estado")
    List<Object[]> findResumenTareasPorEstado();
}
