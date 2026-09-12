-- Apply after 20260912_product_attributes.sql. Additive and repeatable.
-- Scalar filter predicates use jsonb_extract_path_text; match that expression exactly.
BEGIN;
CREATE INDEX IF NOT EXISTS idx_catalog_brand ON product (jsonb_extract_path_text(brand, 'code'));
CREATE INDEX IF NOT EXISTS idx_wine_region ON wine (jsonb_extract_path_text(extended_details, 'region', 'code'));
CREATE INDEX IF NOT EXISTS idx_wine_appellation ON wine (jsonb_extract_path_text(extended_details, 'appellation', 'code'));
CREATE INDEX IF NOT EXISTS idx_sparkling_region ON sparkling_wine (jsonb_extract_path_text(extended_details, 'region', 'code'));
CREATE INDEX IF NOT EXISTS idx_sparkling_appellation ON sparkling_wine (jsonb_extract_path_text(extended_details, 'appellation', 'code'));
CREATE INDEX IF NOT EXISTS idx_sparkling_method ON sparkling_wine (jsonb_extract_path_text(extended_details, 'sparklingMethod', 'code'));
CREATE INDEX IF NOT EXISTS idx_spirit_region ON spirit (jsonb_extract_path_text(extended_details, 'region', 'code'));
CREATE INDEX IF NOT EXISTS idx_spirit_appellation ON spirit (jsonb_extract_path_text(extended_details, 'appellation', 'code'));
CREATE INDEX IF NOT EXISTS idx_whisky_type ON spirit (jsonb_extract_path_text(extended_details, 'whiskyDetails', 'whiskyType', 'code'));
CREATE INDEX IF NOT EXISTS idx_whisky_blend ON spirit (jsonb_extract_path_text(extended_details, 'whiskyDetails', 'blendStyle', 'code'));
CREATE INDEX IF NOT EXISTS idx_cognac_classification ON spirit (jsonb_extract_path_text(extended_details, 'cognacDetails', 'ageClassification', 'code'));
CREATE INDEX IF NOT EXISTS idx_cognac_origin ON spirit (jsonb_extract_path_text(extended_details, 'cognacDetails', 'originArea', 'code'));
COMMIT;
