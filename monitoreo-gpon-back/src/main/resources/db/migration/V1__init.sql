-- V1__init.sql: esquema base para monitoreo SNMP con hubs, vendors y OID mappings

-- =========================================
-- Tabla de vendors/marcas (Huawei, Sagemcom, etc.)
-- =========================================
CREATE TABLE IF NOT EXISTS vendor (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL,
    description TEXT
);

-- =========================================
-- Tabla de tipos de dispositivo (OLT, ONT)
-- =========================================
CREATE TABLE IF NOT EXISTS device_type (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL
);

-- =========================================
-- Tabla de OLTs
-- =========================================
CREATE TABLE IF NOT EXISTS olt (
    id BIGSERIAL PRIMARY KEY,
    vendor_id BIGINT REFERENCES vendor(id) ON DELETE SET NULL,
    name VARCHAR(255) NOT NULL,
    ip_address VARCHAR(100) NOT NULL,
    serial_number VARCHAR(255),
    model VARCHAR(255),
    cant_ports INTEGER,
    snmp_version VARCHAR(10),
    snmp_community VARCHAR(255),
    snmp_port INTEGER DEFAULT 161,
    snmp_timeout_ms INTEGER DEFAULT 5000,
    soft_version VARCHAR(100),
    command_protection_password VARCHAR(255)
);

-- =========================================
-- Tabla de ONTs
-- =========================================
CREATE TABLE IF NOT EXISTS ont (
    id BIGSERIAL PRIMARY KEY,
    olt_id BIGINT NOT NULL REFERENCES olt(id) ON DELETE CASCADE,
    vendor_id BIGINT REFERENCES vendor(id) ON DELETE SET NULL,
    id_cliente VARCHAR(50),
    mac_addr VARCHAR(50),
    sn_ont VARCHAR(255),
    model VARCHAR(255),
    tecnologia VARCHAR(50),
    soft_version VARCHAR(100),
    status VARCHAR(50),
    box_name VARCHAR(255),
    ip_address VARCHAR(100),
    cod_olt VARCHAR(100),
    fecha_act DATE
);

-- =========================================
-- Tabla de definiciones de OIDs
-- =========================================
CREATE TABLE IF NOT EXISTS oid_definition (
    id BIGSERIAL PRIMARY KEY,
    vendor_id BIGINT REFERENCES vendor(id) ON DELETE CASCADE,
    device_type_id BIGINT REFERENCES device_type(id) ON DELETE CASCADE,
    metric_key VARCHAR(100) NOT NULL,
    oid VARCHAR(255) NOT NULL,
    value_type VARCHAR(50) DEFAULT 'STRING',
    description TEXT,
    UNIQUE (vendor_id, device_type_id, metric_key)
);

-- =========================================
-- Tabla de usuarios de la aplicación
-- =========================================
CREATE TABLE IF NOT EXISTS app_user (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(512) NOT NULL,
    roles VARCHAR(255)
);

-- =========================================
-- Tabla de puertos de las olts
-- =========================================
CREATE TABLE IF NOT EXISTS port (
    id BIGSERIAL PRIMARY KEY,
    olt_id BIGINT NOT NULL,
    slot INTEGER NOT NULL,
    port_number INTEGER NOT NULL,
    description VARCHAR(255),
    status VARCHAR(50),
    CONSTRAINT fk_port_olt FOREIGN KEY (olt_id) REFERENCES olt(id) ON DELETE CASCADE
);
