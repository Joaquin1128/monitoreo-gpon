-- V2__add_sample_data.sql
-- Inserta datos de prueba (idempotente)

BEGIN;

-- Insertar vendor ZTE si no existe
INSERT INTO vendor (name, description)
SELECT 'ZTE', 'Proveedor ZTE'
WHERE NOT EXISTS (SELECT 1 FROM vendor WHERE name = 'ZTE');

-- Asegurar device types
INSERT INTO device_type (name)
SELECT 'OLT' WHERE NOT EXISTS (SELECT 1 FROM device_type WHERE name = 'OLT');
INSERT INTO device_type (name)
SELECT 'ONT' WHERE NOT EXISTS (SELECT 1 FROM device_type WHERE name = 'ONT');

-- Insertar hub de pruebas
INSERT INTO hub (name, latitude, longitude)
SELECT 'Dev Hub', -34.6000, -58.5000
WHERE NOT EXISTS (SELECT 1 FROM hub WHERE name = 'Dev Hub');

-- Insertar OLT ZTE si no existe (usa ids de hub y vendor)
INSERT INTO olt (hub_id, vendor_id, name, ip_address, cant_ports, snmp_version, snmp_community)
SELECT h.id, v.id, 'OLT-ZTE-001', '10.0.0.10', 16, 'v2c', 'public'
FROM hub h, vendor v
WHERE h.name = 'Dev Hub' AND v.name = 'ZTE'
  AND NOT EXISTS (SELECT 1 FROM olt WHERE name = 'OLT-ZTE-001');

-- Insertar una ONT de ejemplo asociada a la OLT anterior
INSERT INTO ont (olt_id, vendor_id, id_cliente, mac_addr, sn_ont, model, status, ip_address)
SELECT o.id, v.id, 'DEV001', 'AA:BB:CC:DD:EE:01', 'SNDEV001', 'ZTE-F612', 'ACTIVE', '10.0.0.101'
FROM olt o, vendor v
WHERE o.name = 'OLT-ZTE-001' AND v.name = 'ZTE'
  AND NOT EXISTS (SELECT 1 FROM ont WHERE sn_ont = 'SNDEV001');

-- Insertar definiciones OID para ZTE (OLT)
INSERT INTO oid_definition (vendor_id, device_type_id, metric_key, oid, value_type)
SELECT v.id, dt.id, 'cpuUsage', '.1.3.6.1.4.1.1234.1.1', 'NUMERIC'
FROM vendor v, device_type dt
WHERE v.name = 'ZTE' AND dt.name = 'OLT'
  AND NOT EXISTS (
    SELECT 1 FROM oid_definition od
    WHERE od.vendor_id = v.id AND od.device_type_id = dt.id AND od.metric_key = 'cpuUsage'
  );

-- Insertar definiciones OID para ZTE (ONT)
INSERT INTO oid_definition (vendor_id, device_type_id, metric_key, oid, value_type)
SELECT v.id, dt.id, 'ontStatus', '.1.3.6.1.4.1.1234.1.2', 'STRING'
FROM vendor v, device_type dt
WHERE v.name = 'ZTE' AND dt.name = 'ONT'
  AND NOT EXISTS (
    SELECT 1 FROM oid_definition od
    WHERE od.vendor_id = v.id AND od.device_type_id = dt.id AND od.metric_key = 'ontStatus'
  );

-- Insertar usuario admin de desarrollo (con password placeholder)
INSERT INTO app_user (username, password_hash, roles)
SELECT 'admin', 'changeme', 'ROLE_ADMIN'
WHERE NOT EXISTS (SELECT 1 FROM app_user WHERE username = 'admin');

COMMIT;
