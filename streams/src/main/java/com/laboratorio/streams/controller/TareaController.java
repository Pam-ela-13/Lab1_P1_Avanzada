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

    @PostMapping
    public ResponseEntity<TareaDTO> crearTarea(@Valid @RequestBody TareaDTO tareaDTO) {
        TareaDTO tareaCreada = tareaService.crearTarea(tareaDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(tareaCreada);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TareaDTO> obtenerTareaPorId(@PathVariable Long id) {
        TareaDTO tarea = tareaService.obtenerTareaPorId(id);
        return ResponseEntity.ok(tarea);
    }

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

    @GetMapping("/all")
    public ResponseEntity<List<TareaDTO>> obtenerTodasLasTareas() {
        List<TareaDTO> tareas = tareaService.obtenerTodasLasTareas();
        return ResponseEntity.ok(tareas);
    }

    @GetMapping("/resumen")
    public ResponseEntity<ResumenTareasDTO> obtenerResumen() {
        ResumenTareasDTO resumen = tareaService.obtenerResumenTareas();
        return ResponseEntity.ok(resumen);
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<TareaDTO>> obtenerTareasPorEstado(@PathVariable Estado estado) {
        List<TareaDTO> tareas = tareaService.obtenerTareasPorEstado(estado);
        return ResponseEntity.ok(tareas);
    }

    @GetMapping("/prioridad-minima/{prioridad}")
    public ResponseEntity<List<TareaDTO>> obtenerTareasPorPrioridadMinima(@PathVariable Integer prioridad) {
        List<TareaDTO> tareas = tareaService.obtenerTareasPorPrioridadMinima(prioridad);
        return ResponseEntity.ok(tareas);
    }

    @GetMapping("/prioridad/{prioridad}")
    public ResponseEntity<List<TareaDTO>> obtenerTareasPorPrioridad(@PathVariable Integer prioridad) {
        List<TareaDTO> tareas = tareaService.obtenerTareasPorPrioridad(prioridad);
        return ResponseEntity.ok(tareas);
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<TareaDTO>> buscarTareasPorTitulo(@RequestParam String titulo) {
        List<TareaDTO> tareas = tareaService.buscarTareasPorTitulo(titulo);
        return ResponseEntity.ok(tareas);
    }

    @GetMapping("/pendientes/ordenadas")
    public ResponseEntity<List<TareaDTO>> obtenerTareasPendientesOrdenadas() {
        List<TareaDTO> tareas = tareaService.obtenerTareasPendientesOrdenadas();
        return ResponseEntity.ok(tareas);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TareaDTO> actualizarTarea(
            @PathVariable Long id,
            @Valid @RequestBody TareaDTO tareaDTO) {
        TareaDTO tareaActualizada = tareaService.actualizarTarea(id, tareaDTO);
        return ResponseEntity.ok(tareaActualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarTarea(@PathVariable Long id) {
        tareaService.eliminarTarea(id);
        return ResponseEntity.noContent().build();
    }
}
