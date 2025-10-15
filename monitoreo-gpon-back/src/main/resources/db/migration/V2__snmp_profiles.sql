-- V2: SNMP profiles and OID mapping
CREATE TABLE IF NOT EXISTS snmp_profile (
  id BIGSERIAL PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  vendor VARCHAR(50),
  description TEXT,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);

CREATE TABLE IF NOT EXISTS snmp_oid (
  id BIGSERIAL PRIMARY KEY,
  profile_id BIGINT REFERENCES snmp_profile(id) ON DELETE CASCADE,
  metric_key VARCHAR(255) NOT NULL,
  oid VARCHAR(255) NOT NULL,
  value_type VARCHAR(50) DEFAULT 'STRING'
);

-- Seeds for Huawei and Sagemcom (basic)
INSERT INTO snmp_profile (name, vendor, description) VALUES ('HUAWEI default', 'HUAWEI', 'Default profile for Huawei') ON CONFLICT DO NOTHING;
INSERT INTO snmp_profile (name, vendor, description) VALUES ('SAGEMCOM default', 'SAGEMCOM', 'Default profile for Sagemcom') ON CONFLICT DO NOTHING;

-- Map common keys
INSERT INTO snmp_oid (profile_id, metric_key, oid, value_type)
SELECT p.id, 'sysName', '1.3.6.1.2.1.1.5.0', 'STRING' FROM snmp_profile p WHERE p.name = 'HUAWEI default' ON CONFLICT DO NOTHING;
INSERT INTO snmp_oid (profile_id, metric_key, oid, value_type)
SELECT p.id, 'sysUpTime', '1.3.6.1.2.1.1.3.0', 'NUMERIC' FROM snmp_profile p WHERE p.name = 'HUAWEI default' ON CONFLICT DO NOTHING;

INSERT INTO snmp_oid (profile_id, metric_key, oid, value_type)
SELECT p.id, 'sysName', '1.3.6.1.2.1.1.5.0', 'STRING' FROM snmp_profile p WHERE p.name = 'SAGEMCOM default' ON CONFLICT DO NOTHING;
INSERT INTO snmp_oid (profile_id, metric_key, oid, value_type)
SELECT p.id, 'sysUpTime', '1.3.6.1.2.1.1.3.0', 'NUMERIC' FROM snmp_profile p WHERE p.name = 'SAGEMCOM default' ON CONFLICT DO NOTHING;
