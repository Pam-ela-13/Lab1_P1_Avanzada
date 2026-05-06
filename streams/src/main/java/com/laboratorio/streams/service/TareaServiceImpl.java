package com.laboratorio.streams.service;

import com.laboratorio.streams.dto.ResumenTareasDTO;
import com.laboratorio.streams.dto.TareaDTO;
import com.laboratorio.streams.exception.ResourceNotFoundException;
import com.laboratorio.streams.model.Estado;
import com.laboratorio.streams.model.entity.Tarea;
import com.laboratorio.streams.repository.TareaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TareaServiceImpl implements TareaService {

    private final TareaRepository tareaRepository;

    @Override
    public TareaDTO crearTarea(TareaDTO tareaDTO) {
        validarTarea(tareaDTO);
        
        Tarea tarea = new Tarea();
        tarea.setTitulo(tareaDTO.getTitulo());
        tarea.setDescripcion(tareaDTO.getDescripcion());
        tarea.setEstado(tareaDTO.getEstado() != null ? tareaDTO.getEstado() : Estado.PENDIENTE);
        tarea.setPrioridad(tareaDTO.getPrioridad());

        Tarea tareaGuardada = tareaRepository.save(tarea);
        return convertirADTO(tareaGuardada);
    }

    @Override
    @Transactional(readOnly = true)
    public TareaDTO obtenerTareaPorId(Long id) {
        Tarea tarea = tareaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tarea no encontrada con ID: " + id));
        return convertirADTO(tarea);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TareaDTO> obtenerTodasLasTareas() {
        return tareaRepository.findAll()
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TareaDTO> obtenerTareasPaginadas(Pageable pageable) {
        return tareaRepository.findAll(pageable)
                .map(this::convertirADTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TareaDTO> obtenerTareasPorEstadoPaginadas(Estado estado, Pageable pageable) {
        if (estado == null) {
            throw new IllegalArgumentException("El estado no puede ser nulo");
        }
        return tareaRepository.findByEstado(estado, pageable)
                .map(this::convertirADTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TareaDTO> obtenerTareasPorEstado(Estado estado) {
        // Consulta por convención
        return tareaRepository.findByEstado(estado)
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TareaDTO> obtenerTareasPorPrioridad(Integer prioridad) {
        validarPrioridad(prioridad);
        // Consulta por convención
        return tareaRepository.findByPrioridad(prioridad)
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TareaDTO> obtenerTareasPorPrioridadMinima(Integer prioridad) {
        validarPrioridad(prioridad);
        // Consulta personalizada con @Query
        return tareaRepository.findTareasPorPrioridadMinima(prioridad)
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TareaDTO> buscarTareasPorTitulo(String titulo) {
        if (titulo == null || titulo.trim().isEmpty()) {
            throw new IllegalArgumentException("El título no puede estar vacío");
        }
        // Consulta por convención
        return tareaRepository.findByTituloContainingIgnoreCase(titulo)
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TareaDTO> obtenerTareasPendientesOrdenadas() {
        // Consulta personalizada con @Query ordenada por prioridad
        return tareaRepository.findTareasPendientesOrdenadas()
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ResumenTareasDTO obtenerResumenTareas() {
        List<Object[]> resultados = tareaRepository.findResumenTareasPorEstado();
        
        Map<Estado, Long> tareasPorEstado = new HashMap<>();
        for (Object[] resultado : resultados) {
            Estado estado = (Estado) resultado[0];
            Long cantidad = (Long) resultado[1];
            tareasPorEstado.put(estado, cantidad);
        }
        
        return new ResumenTareasDTO(tareasPorEstado);
    }

    @Override
    public TareaDTO actualizarTarea(Long id, TareaDTO tareaDTO) {
        Tarea tarea = tareaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tarea no encontrada con ID: " + id));

        validarTarea(tareaDTO);

        tarea.setTitulo(tareaDTO.getTitulo());
        tarea.setDescripcion(tareaDTO.getDescripcion());
        tarea.setEstado(tareaDTO.getEstado());
        tarea.setPrioridad(tareaDTO.getPrioridad());

        Tarea tareaActualizada = tareaRepository.save(tarea);
        return convertirADTO(tareaActualizada);
    }

    @Override
    public void eliminarTarea(Long id) {
        Tarea tarea = tareaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tarea no encontrada con ID: " + id));
        tareaRepository.delete(tarea);
    }

    // Métodos auxiliares privados
    private void validarTarea(TareaDTO tareaDTO) {
        if (tareaDTO.getTitulo() == null || tareaDTO.getTitulo().trim().isEmpty()) {
            throw new IllegalArgumentException("El título es requerido");
        }
        if (tareaDTO.getDescripcion() == null || tareaDTO.getDescripcion().trim().isEmpty()) {
            throw new IllegalArgumentException("La descripción es requerida");
        }
        if (tareaDTO.getPrioridad() == null) {
            throw new IllegalArgumentException("La prioridad es requerida");
        }
        validarPrioridad(tareaDTO.getPrioridad());
    }

    private void validarPrioridad(Integer prioridad) {
        if (prioridad == null || prioridad < 1 || prioridad > 5) {
            throw new IllegalArgumentException("La prioridad debe estar entre 1 y 5");
        }
    }

    private TareaDTO convertirADTO(Tarea tarea) {
        return new TareaDTO(
                tarea.getId(),
                tarea.getTitulo(),
                tarea.getDescripcion(),
                tarea.getEstado(),
                tarea.getPrioridad(),
                tarea.getFecha_creacion()
        );
    }
}
