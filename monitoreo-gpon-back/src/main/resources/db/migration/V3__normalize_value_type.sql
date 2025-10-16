-- V3__normalize_value_type.sql
-- Normaliza la columna oid_definition.value_type a valores canónicos: 'NUMERIC' o 'STRING'
-- y añade un CHECK constraint para prevenir valores distintos en el futuro.

-- 1) Actualizar aliases a NUMERIC
UPDATE oid_definition
SET value_type = 'NUMERIC'
WHERE value_type IS NOT NULL
  AND UPPER(value_type) IN (
    'INTEGER','INT','INT32','INT64','COUNTER','COUNTER64','GAUGE','GAUGE32','GAUGE64','TIMETICKS'
  );

-- 2) Actualizar aliases a STRING
UPDATE oid_definition
SET value_type = 'STRING'
WHERE value_type IS NOT NULL
  AND UPPER(value_type) IN (
    'STRING','OCTETSTR','OCTET_STRING','OCTET STRING','OCTETSTR'
  );

-- 3) Si hay nulls, establecer STRING por defecto
UPDATE oid_definition
SET value_type = 'STRING'
WHERE value_type IS NULL;

-- 4) Añadir un CHECK constraint para futuros inserts (Postgres syntax)
-- Comprobamos si el constraint ya existe antes de añadirlo
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint c
        JOIN pg_class t ON c.conrelid = t.oid
        WHERE c.conname = 'chk_oid_definition_value_type' AND t.relname = 'oid_definition'
    ) THEN
        ALTER TABLE oid_definition
        ADD CONSTRAINT chk_oid_definition_value_type CHECK (value_type IN ('NUMERIC','STRING'));
    END IF;
END$$;

-- 5) Opcional: normalizar espacios y mayúsculas por si hubo ' numeric ' etc.
UPDATE oid_definition
SET value_type = UPPER(TRIM(value_type))
WHERE value_type IS NOT NULL AND (value_type <> UPPER(TRIM(value_type)));
