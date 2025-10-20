-- V1__init.sql: esquema consolidado para monitoreo SNMP con hubs, vendors y OID mappings
-- Versión consolidada que incluye todas las modificaciones de V1-V9

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
    device_type_id BIGINT REFERENCES device_type(id) ON DELETE SET NULL;
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
    device_type_id BIGINT REFERENCES device_type(id) ON DELETE SET NULL;
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
-- Tabla unificada de interfaces (GPON, ETH, etc.)
-- =========================================
CREATE TABLE IF NOT EXISTS interface (
    id BIGSERIAL PRIMARY KEY,
    olt_id BIGINT NOT NULL REFERENCES olt(id) ON DELETE CASCADE,
    name VARCHAR(100) NOT NULL,         -- Ej: "gpon0/1", "eth1/1"
    type VARCHAR(20) NOT NULL,          -- Ej: 'GPON', 'ETH', 'UPLINK'
    slot INTEGER,
    port INTEGER,
    description TEXT,
    status VARCHAR(50),                 -- Ej: up/down
    speed_mbps INTEGER,
    mac_address VARCHAR(50),
    UNIQUE (olt_id, type, slot, port)
);

-- Vincular ONT con su interfaz GPON (opcional, si querés saber en qué puerto está)
--ALTER TABLE ont ADD COLUMN interface_id BIGINT REFERENCES interface(id) ON DELETE SET NULL;

-- =========================================
-- Tabla de definiciones de OIDs (versión final con logical_name)
-- =========================================
CREATE TABLE IF NOT EXISTS oid_definition (
    id BIGSERIAL PRIMARY KEY,
    vendor_id BIGINT REFERENCES vendor(id) ON DELETE CASCADE,
    device_type_id BIGINT REFERENCES device_type(id) ON DELETE CASCADE,
    metric_key VARCHAR(100) NOT NULL,
    logical_name VARCHAR(100),
    oid VARCHAR(255) NOT NULL,
    value_type VARCHAR(50) DEFAULT 'STRING',
    description TEXT,
    UNIQUE (vendor_id, device_type_id, metric_key),
    -- Constraint para value_type válidos según la evolución del esquema
    CONSTRAINT chk_oid_definition_value_type CHECK (value_type IN (
        'STRING', 'NUMERIC', 'INTEGER32', 'INT64', 'COUNTER32', 'COUNTER64', 
        'GAUGE32', 'GAUGE64', 'TIMETICKS', 'OCTET_STRING',
        'PERCENTAGE', 'TEMPERATURE', 'STATUS', 'BOOLEAN', 'UPTIME',
        'VOLTAGE', 'CURRENT', 'POWER', 'BANDWIDTH', 'IP_ADDRESS', 'MAC_ADDRESS'
    ))
);

-- =========================================
-- Tabla de muestras de tráfico (histórico)
-- =========================================
CREATE TABLE IF NOT EXISTS traffic_sample (
    id BIGSERIAL PRIMARY KEY,
    interface_id BIGINT NOT NULL REFERENCES interface(id) ON DELETE CASCADE,
    timestamp TIMESTAMP DEFAULT NOW(),
    in_bytes BIGINT,                    -- bytes recibidos
    out_bytes BIGINT,                   -- bytes transmitidos
    in_rate_mbps DOUBLE PRECISION,      -- tasa de entrada calculada
    out_rate_mbps DOUBLE PRECISION      -- tasa de salida calculada
    --FALTA AGREGAR MAS DATOS HISTORICOS
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
-- DATOS INICIALES Y DE PRUEBA
-- =========================================

-- Insertar vendors básicos
INSERT INTO vendor (name, description) VALUES 
('Huawei', 'Proveedor Huawei Technologies'),
('ZTE', 'Proveedor ZTE Corporation')
ON CONFLICT (name) DO NOTHING;

-- Insertar tipos de dispositivo
INSERT INTO device_type (name) VALUES 
('OLT'),
('ONT')
ON CONFLICT (name) DO NOTHING;

-- Insertar usuario admin básico
INSERT INTO app_user (username, password_hash, roles) VALUES 
('admin', 'changeme', 'ROLE_ADMIN')
ON CONFLICT (username) DO NOTHING;

-- =========================================
-- DEFINICIONES OID CON TIPOS ACTUALIZADOS
-- =========================================

-- Huawei OLT - Métricas estándar (vendor_id = 1, device_type_id = 1)
INSERT INTO oid_definition (vendor_id, device_type_id, metric_key, logical_name, oid, value_type, description) VALUES 
(1, 1, 'sysUpTime', 'uptime', '1.3.6.1.2.1.1.3.0', 'TIMETICKS', 'Tiempo de actividad del dispositivo'),
(1, 1, 'cpuUsage', 'cpu_usage', '1.3.6.1.4.1.2011.5.25.1.1.1.1.2', 'PERCENTAGE', 'Uso de CPU (%)'),
(1, 1, 'memoryUsage', 'memory_usage', '1.3.6.1.4.1.2011.5.25.1.1.1.1.3', 'PERCENTAGE', 'Uso de memoria (%)'),
(1, 1, 'sysTempValue', 'temperature', '1.3.6.1.4.1.2011.5.25.1.1.1.1.1', 'TEMPERATURE', 'Temperatura del equipo (°C)'),
(1, 1, 'ifOperStatus', 'overall_status', '1.3.6.1.2.1.2.2.1.8', 'STATUS', 'Estado general del sistema'),
(1, 1, 'sysDescr', 'sys_description', '1.3.6.1.2.1.1.1.0', 'STRING', 'Descripción del sistema / firmware'),
(1, 1, 'fanSpeed', 'fan_speed', '1.3.6.1.4.1.2011.5.25.1.1.1.1.4', 'GAUGE32', 'Velocidad del ventilador (RPM)'),
(1, 1, 'powerVoltage', 'power_voltage', '1.3.6.1.4.1.2011.5.25.1.1.1.1.5', 'VOLTAGE', 'Voltaje de alimentación (V)'),
(1, 1, 'powerCurrent', 'power_current', '1.3.6.1.4.1.2011.5.25.1.1.1.1.6', 'CURRENT', 'Corriente de alimentación (A)'),
(1, 1, 'powerConsumption', 'power_consumption', '1.3.6.1.4.1.2011.5.25.1.1.1.1.7', 'POWER', 'Consumo de potencia (W)'),
(1, 1, 'bandwidthIn', 'bandwidth_in', '1.3.6.1.4.1.2011.5.25.1.1.1.1.8', 'BANDWIDTH', 'Ancho de banda entrada'),
(1, 1, 'bandwidthOut', 'bandwidth_out', '1.3.6.1.4.1.2011.5.25.1.1.1.1.9', 'BANDWIDTH', 'Ancho de banda salida'),
(1, 1, 'packetsIn', 'packets_in', '1.3.6.1.4.1.2011.5.25.1.1.1.1.10', 'COUNTER64', 'Paquetes recibidos'),
(1, 1, 'packetsOut', 'packets_out', '1.3.6.1.4.1.2011.5.25.1.1.1.1.11', 'COUNTER64', 'Paquetes enviados'),
(1, 1, 'powerStatus', 'power_status', '1.3.6.1.4.1.2011.5.25.1.1.1.1.12', 'BOOLEAN', 'Estado de alimentación'),
(1, 1, 'fanStatus', 'fan_status', '1.3.6.1.4.1.2011.5.25.1.1.1.1.13', 'STATUS', 'Estado de ventiladores'),
(1, 1, 'alarmStatus', 'alarm_status', '1.3.6.1.4.1.2011.5.25.1.1.1.1.14', 'STATUS', 'Estado de alarmas'),
(1, 1, 'mgmtIp', 'mgmt_ip', '1.3.6.1.4.1.2011.5.25.1.1.1.1.15', 'IP_ADDRESS', 'IP de gestión'),
(1, 1, 'macAddress', 'mac_address', '1.3.6.1.4.1.2011.5.25.1.1.1.1.16', 'MAC_ADDRESS', 'Dirección MAC')
ON CONFLICT (vendor_id, device_type_id, metric_key) DO NOTHING;

-- ZTE OLT - Métricas estándar (vendor_id = 2, device_type_id = 1)
INSERT INTO oid_definition (vendor_id, device_type_id, metric_key, logical_name, oid, value_type, description) VALUES
(2, 1, 'sysUpTime', 'uptime', '1.3.6.1.2.1.1.3.0', 'TIMETICKS', 'Tiempo de actividad del dispositivo'),
(2, 1, 'cpuUsage', 'cpu_usage', '1.3.6.1.4.1.3902.1015.1.1.1.1.2', 'PERCENTAGE', 'Uso de CPU (%)'),
(2, 1, 'memoryUsage', 'memory_usage', '1.3.6.1.4.1.3902.1015.1.1.1.1.3', 'PERCENTAGE', 'Uso de memoria (%)'),
(2, 1, 'sysTempValue', 'temperature', '1.3.6.1.4.1.3902.1015.1.1.1.1.1', 'TEMPERATURE', 'Temperatura del equipo (°C)'),
(2, 1, 'ifOperStatus', 'overall_status', '1.3.6.1.2.1.2.2.1.8', 'STATUS', 'Estado general del sistema'),
(2, 1, 'sysDescr', 'sys_description', '1.3.6.1.2.1.1.1.0', 'STRING', 'Descripción del sistema / firmware'),
(2, 1, 'zteModuleStatus', 'module_status', '1.3.6.1.4.1.3902.1015.1.1.1.1.4', 'STATUS', 'Estado del módulo'),
(2, 1, 'zteAlarmCount', 'alarm_count', '1.3.6.1.4.1.3902.1015.1.1.1.1.5', 'COUNTER32', 'Cantidad de alarmas activas'),
(2, 1, 'powerVoltage', 'power_voltage', '1.3.6.1.4.1.3902.1015.1.1.1.1.5', 'VOLTAGE', 'Voltaje de alimentación (V)'),
(2, 1, 'powerCurrent', 'power_current', '1.3.6.1.4.1.3902.1015.1.1.1.1.6', 'CURRENT', 'Corriente de alimentación (A)'),
(2, 1, 'powerConsumption', 'power_consumption', '1.3.6.1.4.1.3902.1015.1.1.1.1.7', 'POWER', 'Consumo de potencia (W)'),
(2, 1, 'bandwidthIn', 'bandwidth_in', '1.3.6.1.4.1.3902.1015.1.1.1.1.8', 'BANDWIDTH', 'Ancho de banda entrada'),
(2, 1, 'bandwidthOut', 'bandwidth_out', '1.3.6.1.4.1.3902.1015.1.1.1.1.9', 'BANDWIDTH', 'Ancho de banda salida'),
(2, 1, 'packetsIn', 'packets_in', '1.3.6.1.4.1.3902.1015.1.1.1.1.10', 'COUNTER64', 'Paquetes recibidos'),
(2, 1, 'packetsOut', 'packets_out', '1.3.6.1.4.1.3902.1015.1.1.1.1.11', 'COUNTER64', 'Paquetes enviados'),
(2, 1, 'powerStatus', 'power_status', '1.3.6.1.4.1.3902.1015.1.1.1.1.12', 'BOOLEAN', 'Estado de alimentación'),
(2, 1, 'mgmtIp', 'mgmt_ip', '1.3.6.1.4.1.3902.1015.1.1.1.1.15', 'IP_ADDRESS', 'IP de gestión'),
(2, 1, 'macAddress', 'mac_address', '1.3.6.1.4.1.3902.1015.1.1.1.1.16', 'MAC_ADDRESS', 'Dirección MAC')
ON CONFLICT (vendor_id, device_type_id, metric_key) DO NOTHING;

-- Definiciones OID básicas para ONT (ejemplos)
INSERT INTO oid_definition (vendor_id, device_type_id, metric_key, logical_name, oid, value_type, description) VALUES
(1, 2, 'ontStatus', 'ont_status', '1.3.6.1.4.1.2011.5.25.1.2.1.1.1', 'STATUS', 'Estado de la ONT'),
(1, 2, 'ontUptime', 'ont_uptime', '1.3.6.1.4.1.2011.5.25.1.2.1.1.2', 'TIMETICKS', 'Tiempo de actividad ONT'),
(2, 2, 'ontStatus', 'ont_status', '1.3.6.1.4.1.3902.1015.1.2.1.1.1', 'STATUS', 'Estado de la ONT ZTE'),
(2, 2, 'ontUptime', 'ont_uptime', '1.3.6.1.4.1.3902.1015.1.2.1.1.2', 'TIMETICKS', 'Tiempo de actividad ONT ZTE')
ON CONFLICT (vendor_id, device_type_id, metric_key) DO NOTHING;