-- Migration: Add logical_name column to oid_definition table
-- =========================================

ALTER TABLE oid_definition ADD COLUMN logical_name VARCHAR(100);

-- Add some sample data with logical names
-- =========================================

-- Huawei OLT - Métricas estándar
INSERT INTO oid_definition (vendor_id, device_type_id, metric_key, logical_name, oid, value_type, description) VALUES 
(1, 1, 'sysUpTime', 'uptime', '1.3.6.1.2.1.1.3.0', 'NUMERIC', 'Tiempo de actividad del dispositivo'),
(1, 1, 'cpuUsage', 'cpu_usage', '1.3.6.1.4.1.2011.5.25.1.1.1.1.2', 'NUMERIC', 'Uso de CPU (%)'),
(1, 1, 'memoryUsage', 'memory_usage', '1.3.6.1.4.1.2011.5.25.1.1.1.1.3', 'NUMERIC', 'Uso de memoria (%)'),
(1, 1, 'sysTempValue', 'temperature', '1.3.6.1.4.1.2011.5.25.1.1.1.1.1', 'NUMERIC', 'Temperatura del equipo (°C)'),
(1, 1, 'ifOperStatus', 'overall_status', '1.3.6.1.2.1.2.2.1.8', 'NUMERIC', 'Estado general del sistema (online/offline/warning)'),
(1, 1, 'sysDescr', 'sys_description', '1.3.6.1.2.1.1.1.0', 'STRING', 'Descripción del sistema / firmware');

-- ZTE OLT - Métricas estándar (mismos logical_name, diferentes OIDs)
INSERT INTO oid_definition (vendor_id, device_type_id, metric_key, logical_name, oid, value_type, description) VALUES
(2, 1, 'sysUpTime', 'uptime', '1.3.6.1.2.1.1.3.0', 'NUMERIC', 'Tiempo de actividad del dispositivo'),
(2, 1, 'cpuUsage', 'cpu_usage', '1.3.6.1.4.1.3902.1015.1.1.1.1.2', 'NUMERIC', 'Uso de CPU (%)'),
(2, 1, 'memoryUsage', 'memory_usage', '1.3.6.1.4.1.3902.1015.1.1.1.1.3', 'NUMERIC', 'Uso de memoria (%)'),
(2, 1, 'sysTempValue', 'temperature', '1.3.6.1.4.1.3902.1015.1.1.1.1.1', 'NUMERIC', 'Temperatura del equipo (°C)'),
(2, 1, 'ifOperStatus', 'overall_status', '1.3.6.1.2.1.2.2.1.8', 'NUMERIC', 'Estado general del sistema (online/offline/warning)'),
(2, 1, 'sysDescr', 'sys_description', '1.3.6.1.2.1.1.1.0', 'STRING', 'Descripción del sistema / firmware');

-- Métricas específicas del vendor (van a additionalMetrics)
INSERT INTO oid_definition (vendor_id, device_type_id, metric_key, logical_name, oid, value_type, description) VALUES
(1, 1, 'huawei.fan.speed', 'fan_speed', '1.3.6.1.4.1.2011.5.25.1.1.1.1.4', 'NUMERIC', 'Velocidad del ventilador (RPM)'),
(1, 1, 'huawei.power.voltage', 'power_voltage', '1.3.6.1.4.1.2011.5.25.1.1.1.1.5', 'NUMERIC', 'Voltaje de alimentación (V)'),
(2, 1, 'zte.module.status', 'module_status', '1.3.6.1.4.1.3902.1015.1.1.1.1.4', 'STRING', 'Estado del módulo'),
(2, 1, 'zte.alarm.count', 'alarm_count', '1.3.6.1.4.1.3902.1015.1.1.1.1.5', 'NUMERIC', 'Cantidad de alarmas activas');
