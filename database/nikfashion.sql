-- =========================================================
-- Base de datos: NikFashion (mini tienda de calzado Nike)
-- Modulo: Gestion de productos (inventario)
-- Ejecutar en MySQL / phpMyAdmin (XAMPP) antes de correr el proyecto
-- =========================================================

CREATE DATABASE IF NOT EXISTS nikfashion
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE nikfashion;

DROP TABLE IF EXISTS producto;

CREATE TABLE producto (
    id_producto   INT AUTO_INCREMENT PRIMARY KEY,
    referencia    VARCHAR(20)   NOT NULL UNIQUE,
    nombre        VARCHAR(100)  NOT NULL,
    categoria     VARCHAR(50)   NOT NULL,
    talla         DECIMAL(3,1)  NOT NULL,
    color         VARCHAR(30)   NOT NULL,
    precio        DECIMAL(12,2) NOT NULL CHECK (precio >= 0),
    stock         INT           NOT NULL DEFAULT 0 CHECK (stock >= 0),
    fecha_registro TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Datos de prueba
INSERT INTO producto (referencia, nombre, categoria, talla, color, precio, stock) VALUES
('NK-AF1-001', 'Nike Air Force 1 ''07',   'Casual',     40.0, 'Blanco', 549900, 12),
('NK-AM90-002','Nike Air Max 90',         'Casual',     41.0, 'Negro',  629900,  8),
('NK-PEG-003', 'Nike Pegasus 41',         'Running',    42.0, 'Azul',   599900, 10),
('NK-DNK-004', 'Nike Dunk Low Retro',     'Casual',     39.5, 'Gris',   519900,  5),
('NK-MRC-005', 'Nike Mercurial Vapor 15', 'Futbol',     41.5, 'Rojo',   459900,  6);
