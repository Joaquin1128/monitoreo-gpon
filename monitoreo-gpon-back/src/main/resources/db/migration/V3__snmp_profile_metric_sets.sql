-- V3: add default metric sets JSON to snmp_profile
ALTER TABLE snmp_profile ADD COLUMN IF NOT EXISTS default_metric_sets TEXT;

-- Seed default metric sets for Huawei and Sagemcom
UPDATE snmp_profile SET default_metric_sets = '{"OLT": ["sysName","sysUpTime","temperature","fanStatus","model"], "ONT": ["sysName","ontRxPower","ontTxPower"]}'
WHERE name = 'HUAWEI default';

UPDATE snmp_profile SET default_metric_sets = '{"OLT": ["sysName","sysUpTime","temperature","model"], "ONT": ["sysName","ontRxPower","ontTxPower"]}'
WHERE name = 'SAGEMCOM default';
