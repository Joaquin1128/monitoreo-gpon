-- Migration: Remove category column from oid_definition table
-- This column is no longer needed since all OIDs are mapped to specific fields

-- Drop the category column
ALTER TABLE oid_definition DROP COLUMN category;
