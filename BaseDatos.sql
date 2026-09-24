-- ============================================================
-- BASE DE DATOS - PRUEBA TECNICA TATA
-- Arquitectura de Microservicios
-- ============================================================

-- ============================================================
-- CLIENTE SERVICE
-- Base de datos: cliente_db
-- ============================================================

CREATE TABLE IF NOT EXISTS persona (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    genero VARCHAR(50),
    edad INTEGER NOT NULL,
    identificacion VARCHAR(50) NOT NULL UNIQUE,
    direccion VARCHAR(255),
    telefono VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS cliente (
    id BIGINT PRIMARY KEY,
    cliente_id VARCHAR(100) NOT NULL UNIQUE,
    contrasena VARCHAR(255) NOT NULL,
    estado BOOLEAN NOT NULL,
    CONSTRAINT fk_cliente_persona
    FOREIGN KEY (id)
    REFERENCES persona(id)
    ON DELETE CASCADE
);

-- ============================================================
-- CUENTA SERVICE
-- Base de datos: cuenta_db
-- ============================================================

CREATE TABLE IF NOT EXISTS cuenta (
    id BIGSERIAL PRIMARY KEY,
    numero_cuenta VARCHAR(50) NOT NULL UNIQUE,
    tipo_cuenta VARCHAR(50) NOT NULL,
    saldo_inicial NUMERIC(15,2) NOT NULL,
    saldo_actual NUMERIC(15,2) NOT NULL,
    estado BOOLEAN NOT NULL,
    cliente_id VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS movimiento (
    id BIGSERIAL PRIMARY KEY,
    fecha TIMESTAMP NOT NULL,
    tipo_movimiento VARCHAR(50) NOT NULL,
    valor NUMERIC(15,2) NOT NULL,
    saldo NUMERIC(15,2) NOT NULL,
    cuenta_id BIGINT NOT NULL,
    CONSTRAINT fk_movimiento_cuenta
    FOREIGN KEY (cuenta_id)
    REFERENCES cuenta(id)
    ON DELETE CASCADE
);

-- ============================================================
-- INDICES
-- ============================================================

CREATE INDEX IF NOT EXISTS idx_cliente_cliente_id
ON cliente(cliente_id);

CREATE INDEX IF NOT EXISTS idx_cuenta_cliente_id
ON cuenta(cliente_id);

CREATE INDEX IF NOT EXISTS idx_movimiento_cuenta_id
ON movimiento(cuenta_id);

CREATE INDEX IF NOT EXISTS idx_movimiento_fecha
ON movimiento(fecha);
