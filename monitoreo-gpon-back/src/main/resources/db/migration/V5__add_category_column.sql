-- Migration: Add category column to oid_definition table
-- =========================================

ALTER TABLE oid_definition ADD COLUMN category VARCHAR(50);

-- Update existing data with categories
-- =========================================

-- Summary metrics (básicas para lista de dispositivos)
UPDATE oid_definition SET category = 'summary' 
WHERE logical_name IN ('uptime', 'overall_status');

-- Detailed metrics (métricas específicas de vista detallada)
UPDATE oid_definition SET category = 'detailed' 
WHERE logical_name IN ('temperature', 'cpu_usage', 'memory_usage', 'sys_description');

-- Additional metrics (específicas del vendor)
UPDATE oid_definition SET category = 'additional' 
WHERE logical_name NOT IN ('uptime', 'overall_status', 'temperature', 'cpu_usage', 'memory_usage', 'sys_description');

-- Add sample data with categories
-- =========================================

-- Huawei OLT - Métricas por categoría
INSERT INTO oid_definition (vendor_id, device_type_id, metric_key, logical_name, category, oid, value_type, description) VALUES 
(1, 1, 'sysUpTime', 'uptime', 'summary', '1.3.6.1.2.1.1.3.0', 'NUMERIC', 'Tiempo de actividad del dispositivo'),
(1, 1, 'ifOperStatus', 'overall_status', 'summary', '1.3.6.1.2.1.2.2.1.8', 'NUMERIC', 'Estado general del sistema (online/offline/warning)'),

(1, 1, 'sysTempValue', 'temperature', 'detailed', '1.3.6.1.4.1.2011.5.25.1.1.1.1.1', 'NUMERIC', 'Temperatura del equipo (°C)'),
(1, 1, 'cpuUsage', 'cpu_usage', 'detailed', '1.3.6.1.4.1.2011.5.25.1.1.1.1.2', 'NUMERIC', 'Uso de CPU (%)'),
(1, 1, 'memoryUsage', 'memory_usage', 'detailed', '1.3.6.1.4.1.2011.5.25.1.1.1.1.3', 'NUMERIC', 'Uso de memoria (%)'),
(1, 1, 'sysDescr', 'sys_description', 'detailed', '1.3.6.1.2.1.1.1.0', 'STRING', 'Descripción del sistema / firmware'),

(1, 1, 'huawei.fan.speed', 'fan_speed', 'additional', '1.3.6.1.4.1.2011.5.25.1.1.1.1.4', 'NUMERIC', 'Velocidad del ventilador (RPM)'),
(1, 1, 'huawei.power.voltage', 'power_voltage', 'additional', '1.3.6.1.4.1.2011.5.25.1.1.1.1.5', 'NUMERIC', 'Voltaje de alimentación (V)');

-- ZTE OLT - Métricas por categoría
INSERT INTO oid_definition (vendor_id, device_type_id, metric_key, logical_name, category, oid, value_type, description) VALUES
(2, 1, 'sysUpTime', 'uptime', 'summary', '1.3.6.1.2.1.1.3.0', 'NUMERIC', 'Tiempo de actividad del dispositivo'),
(2, 1, 'ifOperStatus', 'overall_status', 'summary', '1.3.6.1.2.1.2.2.1.8', 'NUMERIC', 'Estado general del sistema (online/offline/warning)'),

(2, 1, 'sysTempValue', 'temperature', 'detailed', '1.3.6.1.4.1.3902.1015.1.1.1.1.1', 'NUMERIC', 'Temperatura del equipo (°C)'),
(2, 1, 'cpuUsage', 'cpu_usage', 'detailed', '1.3.6.1.4.1.3902.1015.1.1.1.1.2', 'NUMERIC', 'Uso de CPU (%)'),
(2, 1, 'memoryUsage', 'memory_usage', 'detailed', '1.3.6.1.4.1.3902.1015.1.1.1.1.3', 'NUMERIC', 'Uso de memoria (%)'),
(2, 1, 'sysDescr', 'sys_description', 'detailed', '1.3.6.1.2.1.1.1.0', 'STRING', 'Descripción del sistema / firmware'),

(2, 1, 'zte.module.status', 'module_status', 'additional', '1.3.6.1.4.1.3902.1015.1.1.1.1.4', 'STRING', 'Estado del módulo'),
(2, 1, 'zte.alarm.count', 'alarm_count', 'additional', '1.3.6.1.4.1.3902.1015.1.1.1.1.5', 'NUMERIC', 'Cantidad de alarmas activas');
