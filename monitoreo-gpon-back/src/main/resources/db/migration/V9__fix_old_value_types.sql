-- Migration: Update old value types to new SNMP types
-- Fix NUMERIC values that no longer exist in SnmpValueType enum

-- Update NUMERIC to INTEGER32 (most common case)
UPDATE oid_definition 
SET value_type = 'INTEGER32' 
WHERE value_type = 'NUMERIC';

-- Update other old types if they exist
UPDATE oid_definition 
SET value_type = 'GAUGE32' 
WHERE value_type = 'GAUGE';

UPDATE oid_definition 
SET value_type = 'COUNTER32' 
WHERE value_type = 'COUNTER';

UPDATE oid_definition 
SET value_type = 'TIMETICKS' 
WHERE value_type = 'UPTIME';

-- Verify the changes
SELECT metric_key, value_type, oid 
FROM oid_definition 
ORDER BY metric_key;
