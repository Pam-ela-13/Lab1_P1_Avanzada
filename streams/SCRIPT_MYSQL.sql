-- Script SQL para MySQL Workbench
-- Crear base de datos y tabla para la aplicación de gestión de tareas

-- Crear base de datos
CREATE DATABASE IF NOT EXISTS tareasdb;
USE tareasdb;

-- Crear tabla tareas
CREATE TABLE IF NOT EXISTS tareas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(100) NOT NULL,
    descripcion VARCHAR(500) NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
    prioridad INT NOT NULL CHECK (prioridad >= 1 AND prioridad <= 5),
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_estado (estado),
    INDEX idx_prioridad (prioridad),
    INDEX idx_fecha_creacion (fecha_creacion)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Verificar tabla creada
SHOW TABLES;
DESC tareas;
