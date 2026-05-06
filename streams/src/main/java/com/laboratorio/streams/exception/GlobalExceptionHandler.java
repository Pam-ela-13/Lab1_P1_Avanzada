package com.laboratorio.streams.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Manejador global de excepciones para la aplicación REST.
 * Implementa @RestControllerAdvice para capturar y procesar excepciones
 * en todos los controladores de forma centralizada.
 * 
 * Códigos HTTP implementados:
 * - 200: GET exitoso
 * - 201: POST exitoso (creación)
 * - 204: DELETE exitoso (sin contenido)
 * - 400: Error de validación o argumento inválido
 * - 404: Recurso no encontrado
 * - 500: Error interno del servidor
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Maneja excepciones de validación de entrada (@Valid)
     * Retorna: 400 BAD REQUEST
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> manejarValidacionException(
            MethodArgumentNotValidException ex, WebRequest request) {
        
        Map<String, String> erroresValidacion = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        FieldError::getDefaultMessage,
                        (mensaje1, mensaje2) -> mensaje1 + "; " + mensaje2
                ));

        Map<String, Object> cuerpo = new HashMap<>();
        cuerpo.put("timestamp", LocalDateTime.now());
        cuerpo.put("status", HttpStatus.BAD_REQUEST.value());
        cuerpo.put("error", "Error de Validación");
        cuerpo.put("mensaje", "Datos inválidos en la solicitud. Verifique los campos requeridos y sus restricciones.");
        cuerpo.put("errores", erroresValidacion);
        cuerpo.put("ruta", request.getDescription(false).replace("uri=", ""));

        return new ResponseEntity<>(cuerpo, HttpStatus.BAD_REQUEST);
    }

    /**
     * Maneja excepciones cuando un recurso no es encontrado (404)
     * Retorna: 404 NOT FOUND
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> manejarResourceNotFoundException(
            ResourceNotFoundException ex, WebRequest request) {
        
        Map<String, Object> cuerpo = new HashMap<>();
        cuerpo.put("timestamp", LocalDateTime.now());
        cuerpo.put("status", HttpStatus.NOT_FOUND.value());
        cuerpo.put("error", "Recurso No Encontrado");
        cuerpo.put("mensaje", ex.getMessage());
        cuerpo.put("ruta", request.getDescription(false).replace("uri=", ""));

        return new ResponseEntity<>(cuerpo, HttpStatus.NOT_FOUND);
    }

    /**
     * Maneja excepciones de rutas no encontradas (404)
     * Retorna: 404 NOT FOUND
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<Map<String, Object>> manejarNoHandlerFoundException(
            NoHandlerFoundException ex, WebRequest request) {
        
        Map<String, Object> cuerpo = new HashMap<>();
        cuerpo.put("timestamp", LocalDateTime.now());
        cuerpo.put("status", HttpStatus.NOT_FOUND.value());
        cuerpo.put("error", "Ruta No Encontrada");
        cuerpo.put("mensaje", "El endpoint solicitado no existe: " + ex.getRequestURL());
        cuerpo.put("metodo", ex.getHttpMethod());
        cuerpo.put("ruta", ex.getRequestURL());

        return new ResponseEntity<>(cuerpo, HttpStatus.NOT_FOUND);
    }

    /**
     * Maneja excepciones de argumentos inválidos
     * Retorna: 400 BAD REQUEST
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> manejarIllegalArgumentException(
            IllegalArgumentException ex, WebRequest request) {
        
        Map<String, Object> cuerpo = new HashMap<>();
        cuerpo.put("timestamp", LocalDateTime.now());
        cuerpo.put("status", HttpStatus.BAD_REQUEST.value());
        cuerpo.put("error", "Argumento Inválido");
        cuerpo.put("mensaje", ex.getMessage());
        cuerpo.put("ruta", request.getDescription(false).replace("uri=", ""));

        return new ResponseEntity<>(cuerpo, HttpStatus.BAD_REQUEST);
    }

    /**
     * Maneja excepciones genéricas no capturadas por otros manejadores
     * Retorna: 500 INTERNAL SERVER ERROR
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> manejarExcepcionGeneral(
            Exception ex, WebRequest request) {
        
        Map<String, Object> cuerpo = new HashMap<>();
        cuerpo.put("timestamp", LocalDateTime.now());
        cuerpo.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        cuerpo.put("error", "Error Interno del Servidor");
        cuerpo.put("mensaje", "Ocurrió un error inesperado: " + ex.getMessage());
        cuerpo.put("ruta", request.getDescription(false).replace("uri=", ""));
        
        // En producción, no incluir la traza de la excepción
        // cuerpo.put("stackTrace", ex.getStackTrace());

        return new ResponseEntity<>(cuerpo, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
