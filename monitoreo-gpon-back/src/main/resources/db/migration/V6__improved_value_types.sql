-- Ejemplo de uso del nuevo SnmpValueType mejorado
-- =========================================

-- Insertar definiciones de OID con tipos específicos
INSERT INTO oid_definition (vendor_id, device_type_id, metric_key, logical_name, category, oid, value_type, description) VALUES 

-- Huawei OLT - Métricas con tipos específicos
(1, 1, 'sysUpTime', 'uptime', 'summary', '1.3.6.1.2.1.1.3.0', 'TIMETICKS', 'Tiempo de actividad del dispositivo'),
(1, 1, 'cpuUsage', 'cpu_usage', 'detailed', '1.3.6.1.4.1.2011.5.25.1.1.1.1.2', 'PERCENTAGE', 'Uso de CPU (%)'),
(1, 1, 'memoryUsage', 'memory_usage', 'detailed', '1.3.6.1.4.1.2011.5.25.1.1.1.1.3', 'PERCENTAGE', 'Uso de memoria (%)'),
(1, 1, 'sysTempValue', 'temperature', 'detailed', '1.3.6.1.4.1.2011.5.25.1.1.1.1.1', 'TEMPERATURE', 'Temperatura del equipo (°C)'),
(1, 1, 'ifOperStatus', 'overall_status', 'summary', '1.3.6.1.2.1.2.2.1.8', 'STATUS', 'Estado general del sistema'),
(1, 1, 'sysDescr', 'sys_description', 'detailed', '1.3.6.1.2.1.1.1.0', 'STRING', 'Descripción del sistema'),
(1, 1, 'fanSpeed', 'fan_speed', 'detailed', '1.3.6.1.4.1.2011.5.25.1.1.1.1.4', 'GAUGE32', 'Velocidad del ventilador (RPM)'),
(1, 1, 'powerVoltage', 'power_voltage', 'detailed', '1.3.6.1.4.1.2011.5.25.1.1.1.1.5', 'VOLTAGE', 'Voltaje de alimentación (V)'),
(1, 1, 'powerStatus', 'power_status', 'detailed', '1.3.6.1.4.1.2011.5.25.1.1.1.1.6', 'BOOLEAN', 'Estado de alimentación'),
(1, 1, 'bandwidth', 'bandwidth', 'detailed', '1.3.6.1.4.1.2011.5.25.1.1.1.1.7', 'BANDWIDTH', 'Ancho de banda disponible');

-- ZTE OLT - Mismos logical_name, diferentes OIDs, tipos específicos
INSERT INTO oid_definition (vendor_id, device_type_id, metric_key, logical_name, category, oid, value_type, description) VALUES
(2, 1, 'sysUpTime', 'uptime', 'summary', '1.3.6.1.2.1.1.3.0', 'TIMETICKS', 'Tiempo de actividad del dispositivo'),
(2, 1, 'cpuUsage', 'cpu_usage', 'detailed', '1.3.6.1.4.1.3902.1015.1.1.1.1.2', 'PERCENTAGE', 'Uso de CPU (%)'),
(2, 1, 'memoryUsage', 'memory_usage', 'detailed', '1.3.6.1.4.1.3902.1015.1.1.1.1.3', 'PERCENTAGE', 'Uso de memoria (%)'),
(2, 1, 'sysTempValue', 'temperature', 'detailed', '1.3.6.1.4.1.3902.1015.1.1.1.1.1', 'TEMPERATURE', 'Temperatura del equipo (°C)'),
(2, 1, 'ifOperStatus', 'overall_status', 'summary', '1.3.6.1.2.1.2.2.1.8', 'STATUS', 'Estado general del sistema'),
(2, 1, 'sysDescr', 'sys_description', 'detailed', '1.3.6.1.2.1.1.1.0', 'STRING', 'Descripción del sistema'),
(2, 1, 'zteModuleStatus', 'module_status', 'detailed', '1.3.6.1.4.1.3902.1015.1.1.1.1.4', 'STATUS', 'Estado del módulo'),
(2, 1, 'zteAlarmCount', 'alarm_count', 'detailed', '1.3.6.1.4.1.3902.1015.1.1.1.1.5', 'COUNTER32', 'Cantidad de alarmas activas');
