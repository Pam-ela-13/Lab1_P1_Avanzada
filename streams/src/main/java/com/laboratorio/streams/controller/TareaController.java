package com.laboratorio.streams.controller;

import com.laboratorio.streams.dto.ResumenTareasDTO;
import com.laboratorio.streams.dto.TareaDTO;
import com.laboratorio.streams.model.Estado;
import com.laboratorio.streams.service.TareaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tareas")
@RequiredArgsConstructor
public class TareaController {

    private final TareaService tareaService;

    /**
     * Crear una nueva tarea
     * POST /api/tareas
     * @return 201 CREATED
     */
    @PostMapping
    public ResponseEntity<TareaDTO> crearTarea(@Valid @RequestBody TareaDTO tareaDTO) {
        TareaDTO tareaCreada = tareaService.crearTarea(tareaDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(tareaCreada);
    }

    /**
     * Obtener tarea por ID
     * GET /api/tareas/{id}
     * @return 200 OK
     */
    @GetMapping("/{id}")
    public ResponseEntity<TareaDTO> obtenerTareaPorId(@PathVariable Long id) {
        TareaDTO tarea = tareaService.obtenerTareaPorId(id);
        return ResponseEntity.ok(tarea);
    }

    /**
     * Obtener todas las tareas con paginación opcional
     * GET /api/tareas
     * GET /api/tareas?page=0&size=5
     * GET /api/tareas?estado=PENDIENTE&page=0&size=5
     * @return 200 OK
     */
    @GetMapping
    public ResponseEntity<?> obtenerTareas(
            @RequestParam(required = false) Estado estado,
            Pageable pageable) {
        
        if (estado != null) {
            Page<TareaDTO> tareas = tareaService.obtenerTareasPorEstadoPaginadas(estado, pageable);
            return ResponseEntity.ok(tareas);
        }
        
        Page<TareaDTO> tareas = tareaService.obtenerTareasPaginadas(pageable);
        return ResponseEntity.ok(tareas);
    }

    /**
     * Obtener todas las tareas sin paginación (legacía)
     * GET /api/tareas/all
     * @return 200 OK
     */
    @GetMapping("/all")
    public ResponseEntity<List<TareaDTO>> obtenerTodasLasTareas() {
        List<TareaDTO> tareas = tareaService.obtenerTodasLasTareas();
        return ResponseEntity.ok(tareas);
    }

    /**
     * Obtener resumen de tareas agrupadas por estado
     * GET /api/tareas/resumen
     * @return 200 OK con estadísticas
     */
    @GetMapping("/resumen")
    public ResponseEntity<ResumenTareasDTO> obtenerResumen() {
        ResumenTareasDTO resumen = tareaService.obtenerResumenTareas();
        return ResponseEntity.ok(resumen);
    }

    /**
     * Filtrar tareas por estado (sin paginación)
     * GET /api/tareas/estado/{estado}
     * @return 200 OK
     */
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<TareaDTO>> obtenerTareasPorEstado(@PathVariable Estado estado) {
        List<TareaDTO> tareas = tareaService.obtenerTareasPorEstado(estado);
        return ResponseEntity.ok(tareas);
    }

    /**
     * Filtrar tareas por prioridad mínima
     * GET /api/tareas/prioridad-minima/{prioridad}
     * @return 200 OK
     */
    @GetMapping("/prioridad-minima/{prioridad}")
    public ResponseEntity<List<TareaDTO>> obtenerTareasPorPrioridadMinima(@PathVariable Integer prioridad) {
        List<TareaDTO> tareas = tareaService.obtenerTareasPorPrioridadMinima(prioridad);
        return ResponseEntity.ok(tareas);
    }

    /**
     * Filtrar tareas por prioridad exacta
     * GET /api/tareas/prioridad/{prioridad}
     * @return 200 OK
     */
    @GetMapping("/prioridad/{prioridad}")
    public ResponseEntity<List<TareaDTO>> obtenerTareasPorPrioridad(@PathVariable Integer prioridad) {
        List<TareaDTO> tareas = tareaService.obtenerTareasPorPrioridad(prioridad);
        return ResponseEntity.ok(tareas);
    }

    /**
     * Buscar tareas por título
     * GET /api/tareas/buscar?titulo=...
     * @return 200 OK
     */
    @GetMapping("/buscar")
    public ResponseEntity<List<TareaDTO>> buscarTareasPorTitulo(@RequestParam String titulo) {
        List<TareaDTO> tareas = tareaService.buscarTareasPorTitulo(titulo);
        return ResponseEntity.ok(tareas);
    }

    /**
     * Obtener tareas pendientes ordenadas por prioridad
     * GET /api/tareas/pendientes/ordenadas
     * @return 200 OK
     */
    @GetMapping("/pendientes/ordenadas")
    public ResponseEntity<List<TareaDTO>> obtenerTareasPendientesOrdenadas() {
        List<TareaDTO> tareas = tareaService.obtenerTareasPendientesOrdenadas();
        return ResponseEntity.ok(tareas);
    }

    /**
     * Actualizar una tarea existente
     * PUT /api/tareas/{id}
     * @return 200 OK
     */
    @PutMapping("/{id}")
    public ResponseEntity<TareaDTO> actualizarTarea(
            @PathVariable Long id,
            @Valid @RequestBody TareaDTO tareaDTO) {
        TareaDTO tareaActualizada = tareaService.actualizarTarea(id, tareaDTO);
        return ResponseEntity.ok(tareaActualizada);
    }

    /**
     * Eliminar una tarea
     * DELETE /api/tareas/{id}
     * @return 204 NO CONTENT
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarTarea(@PathVariable Long id) {
        tareaService.eliminarTarea(id);
        return ResponseEntity.noContent().build();
    }
}
