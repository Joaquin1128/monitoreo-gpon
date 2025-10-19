-- =========================================
-- SCRIPT SQL PARA ACTUALIZAR VALORES EN BD
-- =========================================
-- 
-- Este script actualiza los valores existentes y agrega nuevos con tipos específicos

-- =========================================
-- 1. ACTUALIZAR VALORES EXISTENTES
-- =========================================

-- Actualizar tipos básicos a tipos específicos
UPDATE oid_definition SET value_type = 'PERCENTAGE' 
WHERE logical_name IN ('cpu_usage', 'memory_usage') 
AND value_type IN ('NUMERIC', 'STRING');

UPDATE oid_definition SET value_type = 'TEMPERATURE' 
WHERE logical_name = 'temperature' 
AND value_type IN ('NUMERIC', 'STRING');

UPDATE oid_definition SET value_type = 'STATUS' 
WHERE logical_name IN ('overall_status', 'interface_status', 'fan_status', 'alarm_status') 
AND value_type IN ('NUMERIC', 'STRING');

UPDATE oid_definition SET value_type = 'BOOLEAN' 
WHERE logical_name = 'power_status' 
AND value_type IN ('NUMERIC', 'STRING');

UPDATE oid_definition SET value_type = 'UPTIME' 
WHERE logical_name = 'uptime' 
AND value_type IN ('NUMERIC', 'STRING');

UPDATE oid_definition SET value_type = 'STRING' 
WHERE logical_name = 'sys_description' 
AND value_type IN ('NUMERIC');

-- =========================================
-- 2. AGREGAR NUEVOS CAMPOS CON TIPOS ESPECÍFICOS
-- =========================================

-- Métricas eléctricas para Huawei OLT
INSERT INTO oid_definition (vendor_id, device_type_id, metric_key, logical_name, category, oid, value_type, description) VALUES 
(1, 1, 'powerVoltage', 'power_voltage', 'detailed', '1.3.6.1.4.1.2011.5.25.1.1.1.1.5', 'VOLTAGE', 'Voltaje de alimentación (V)'),
(1, 1, 'powerCurrent', 'power_current', 'detailed', '1.3.6.1.4.1.2011.5.25.1.1.1.1.6', 'CURRENT', 'Corriente de alimentación (A)'),
(1, 1, 'powerConsumption', 'power_consumption', 'detailed', '1.3.6.1.4.1.2011.5.25.1.1.1.1.7', 'POWER', 'Consumo de potencia (W)');

-- Métricas de red para Huawei OLT
INSERT INTO oid_definition (vendor_id, device_type_id, metric_key, logical_name, category, oid, value_type, description) VALUES 
(1, 1, 'bandwidthIn', 'bandwidth_in', 'detailed', '1.3.6.1.4.1.2011.5.25.1.1.1.1.8', 'BANDWIDTH', 'Ancho de banda entrada'),
(1, 1, 'bandwidthOut', 'bandwidth_out', 'detailed', '1.3.6.1.4.1.2011.5.25.1.1.1.1.9', 'BANDWIDTH', 'Ancho de banda salida'),
(1, 1, 'packetsIn', 'packets_in', 'detailed', '1.3.6.1.4.1.2011.5.25.1.1.1.1.10', 'COUNTER64', 'Paquetes recibidos'),
(1, 1, 'packetsOut', 'packets_out', 'detailed', '1.3.6.1.4.1.2011.5.25.1.1.1.1.11', 'COUNTER64', 'Paquetes enviados');

-- Estados específicos para Huawei OLT
INSERT INTO oid_definition (vendor_id, device_type_id, metric_key, logical_name, category, oid, value_type, description) VALUES 
(1, 1, 'powerStatus', 'power_status', 'detailed', '1.3.6.1.4.1.2011.5.25.1.1.1.1.12', 'BOOLEAN', 'Estado de alimentación'),
(1, 1, 'fanStatus', 'fan_status', 'detailed', '1.3.6.1.4.1.2011.5.25.1.1.1.1.13', 'STATUS', 'Estado de ventiladores'),
(1, 1, 'alarmStatus', 'alarm_status', 'detailed', '1.3.6.1.4.1.2011.5.25.1.1.1.1.14', 'STATUS', 'Estado de alarmas');

-- Direcciones de red para Huawei OLT
INSERT INTO oid_definition (vendor_id, device_type_id, metric_key, logical_name, category, oid, value_type, description) VALUES 
(1, 1, 'mgmtIp', 'mgmt_ip', 'detailed', '1.3.6.1.4.1.2011.5.25.1.1.1.1.15', 'IP_ADDRESS', 'IP de gestión'),
(1, 1, 'macAddress', 'mac_address', 'detailed', '1.3.6.1.4.1.2011.5.25.1.1.1.1.16', 'MAC_ADDRESS', 'Dirección MAC');

-- =========================================
-- 3. MISMOS CAMPOS PARA ZTE OLT (DIFERENTES OIDs)
-- =========================================

-- Métricas eléctricas para ZTE OLT
INSERT INTO oid_definition (vendor_id, device_type_id, metric_key, logical_name, category, oid, value_type, description) VALUES 
(2, 1, 'powerVoltage', 'power_voltage', 'detailed', '1.3.6.1.4.1.3902.1015.1.1.1.1.5', 'VOLTAGE', 'Voltaje de alimentación (V)'),
(2, 1, 'powerCurrent', 'power_current', 'detailed', '1.3.6.1.4.1.3902.1015.1.1.1.1.6', 'CURRENT', 'Corriente de alimentación (A)'),
(2, 1, 'powerConsumption', 'power_consumption', 'detailed', '1.3.6.1.4.1.3902.1015.1.1.1.1.7', 'POWER', 'Consumo de potencia (W)');

-- Métricas de red para ZTE OLT
INSERT INTO oid_definition (vendor_id, device_type_id, metric_key, logical_name, category, oid, value_type, description) VALUES 
(2, 1, 'bandwidthIn', 'bandwidth_in', 'detailed', '1.3.6.1.4.1.3902.1015.1.1.1.1.8', 'BANDWIDTH', 'Ancho de banda entrada'),
(2, 1, 'bandwidthOut', 'bandwidth_out', 'detailed', '1.3.6.1.4.1.3902.1015.1.1.1.1.9', 'BANDWIDTH', 'Ancho de banda salida'),
(2, 1, 'packetsIn', 'packets_in', 'detailed', '1.3.6.1.4.1.3902.1015.1.1.1.1.10', 'COUNTER64', 'Paquetes recibidos'),
(2, 1, 'packetsOut', 'packets_out', 'detailed', '1.3.6.1.4.1.3902.1015.1.1.1.1.11', 'COUNTER64', 'Paquetes enviados');

-- Estados específicos para ZTE OLT
INSERT INTO oid_definition (vendor_id, device_type_id, metric_key, logical_name, category, oid, value_type, description) VALUES 
(2, 1, 'powerStatus', 'power_status', 'detailed', '1.3.6.1.4.1.3902.1015.1.1.1.1.12', 'BOOLEAN', 'Estado de alimentación'),
(2, 1, 'zteModuleStatus', 'module_status', 'detailed', '1.3.6.1.4.1.3902.1015.1.1.1.1.13', 'STATUS', 'Estado del módulo'),
(2, 1, 'zteAlarmCount', 'alarm_count', 'detailed', '1.3.6.1.4.1.3902.1015.1.1.1.1.14', 'COUNTER32', 'Cantidad de alarmas activas');

-- Direcciones de red para ZTE OLT
INSERT INTO oid_definition (vendor_id, device_type_id, metric_key, logical_name, category, oid, value_type, description) VALUES 
(2, 1, 'mgmtIp', 'mgmt_ip', 'detailed', '1.3.6.1.4.1.3902.1015.1.1.1.1.15', 'IP_ADDRESS', 'IP de gestión'),
(2, 1, 'macAddress', 'mac_address', 'detailed', '1.3.6.1.4.1.3902.1015.1.1.1.1.16', 'MAC_ADDRESS', 'Dirección MAC');

-- =========================================
-- 4. VERIFICAR RESULTADOS
-- =========================================

-- Ver todos los tipos de valor únicos
SELECT DISTINCT value_type, COUNT(*) as cantidad 
FROM oid_definition 
GROUP BY value_type 
ORDER BY cantidad DESC;

-- Ver campos por categoría
SELECT category, logical_name, value_type, description 
FROM oid_definition 
ORDER BY category, logical_name;
