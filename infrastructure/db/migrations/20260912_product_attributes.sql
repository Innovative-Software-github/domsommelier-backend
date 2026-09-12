-- Additive, idempotent migration. Run against the existing catalog before deploying the new backend.
-- Docker/prod currently use Hibernate ddl-auto=update; explicit SQL also supports validate mode.
BEGIN;
ALTER TABLE product ADD COLUMN IF NOT EXISTS brand jsonb;
ALTER TABLE product ADD COLUMN IF NOT EXISTS packaging jsonb;
ALTER TABLE wine ADD COLUMN IF NOT EXISTS extended_details jsonb;
ALTER TABLE sparkling_wine ADD COLUMN IF NOT EXISTS extended_details jsonb;
ALTER TABLE spirit ADD COLUMN IF NOT EXISTS extended_details jsonb;
CREATE TABLE IF NOT EXISTS catalog_attribute_reference (
    id varchar(140) PRIMARY KEY,
    kind varchar(50) NOT NULL,
    code varchar(80) NOT NULL,
    label varchar(160) NOT NULL,
    CONSTRAINT uq_catalog_attribute_reference_kind_code UNIQUE (kind, code)
);
CREATE UNIQUE INDEX IF NOT EXISTS uq_catalog_attribute_reference_label
    ON catalog_attribute_reference (kind, lower(label));
COMMIT;
