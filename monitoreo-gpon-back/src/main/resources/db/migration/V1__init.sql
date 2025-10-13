-- V1__init.sql: crear tablas básicas
CREATE TABLE IF NOT EXISTS olt (
  id BIGSERIAL PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  ip_address VARCHAR(100) NOT NULL,
  snmp_version VARCHAR(10),
  snmp_community VARCHAR(255),
  snmp_port INTEGER DEFAULT 161,
  snmp_timeout_ms INTEGER DEFAULT 5000,
  command_protection_password VARCHAR(255),
  created_at TIMESTAMP WITH TIME ZONE DEFAULT now(),
  updated_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);

CREATE TABLE IF NOT EXISTS ont (
  id BIGSERIAL PRIMARY KEY,
  olt_id BIGINT NOT NULL REFERENCES olt(id) ON DELETE CASCADE,
  onu_id VARCHAR(100),
  onu_index INTEGER,
  serial_number VARCHAR(255),
  model VARCHAR(255),
  vendor VARCHAR(255),
  status VARCHAR(50),
  last_seen_at TIMESTAMP WITH TIME ZONE,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT now(),
  updated_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);

CREATE TABLE IF NOT EXISTS port (
  id BIGSERIAL PRIMARY KEY,
  olt_id BIGINT NOT NULL REFERENCES olt(id) ON DELETE CASCADE,
  slot INTEGER,
  port_number INTEGER,
  description VARCHAR(255),
  status VARCHAR(50),
  created_at TIMESTAMP WITH TIME ZONE DEFAULT now(),
  updated_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);

CREATE TABLE IF NOT EXISTS metric (
  id BIGSERIAL PRIMARY KEY,
  olt_id BIGINT REFERENCES olt(id) ON DELETE SET NULL,
  ont_id BIGINT REFERENCES ont(id) ON DELETE SET NULL,
  metric_type VARCHAR(100),
  value NUMERIC,
  collected_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);

CREATE TABLE IF NOT EXISTS app_user (
  id BIGSERIAL PRIMARY KEY,
  username VARCHAR(100) UNIQUE NOT NULL,
  password_hash VARCHAR(512) NOT NULL,
  roles VARCHAR(255),
  created_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);
