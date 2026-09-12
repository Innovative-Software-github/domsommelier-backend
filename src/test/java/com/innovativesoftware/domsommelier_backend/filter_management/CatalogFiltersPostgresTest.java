package com.innovativesoftware.domsommelier_backend.filter_management;

import com.innovativesoftware.domsommelier_backend.filter_management.catalog.*;
import com.innovativesoftware.domsommelier_backend.filter_management.model.types.*;
import com.innovativesoftware.domsommelier_backend.product_management.product.entity.wine.Wine;
import com.innovativesoftware.domsommelier_backend.product_management.product.entity.spirit.Spirit;
import com.innovativesoftware.domsommelier_backend.product_management.product.entity.champaigne_sparkling.SparklingWine;
import com.innovativesoftware.domsommelier_backend.product_management.product.enums.ProductCategoryEnum;
import com.innovativesoftware.domsommelier_backend.product_management.product.service.*;
import com.innovativesoftware.domsommelier_backend.product_management.product.service.wine.WineSpecification;
import com.innovativesoftware.domsommelier_backend.product_management.product.service.spirit.SpiritSpecification;
import com.innovativesoftware.domsommelier_backend.product_management.product.service.shampaigne_sparkling.ShampaigneSparklingSpecification;
import jakarta.persistence.Entity;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

/** Destructive schema setup ONLY in an explicitly supplied disposable *_test database. */
@EnabledIfSystemProperty(named="catalog.filters.postgres.url", matches=".+_test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CatalogFiltersPostgresTest {
    SessionFactory factory;
    JdbcTemplate jdbc;
    UUID wine1, wine2, sparkling1, whisky1, cognac1;
    @BeforeAll void setup() throws Exception {
        String url = System.getProperty("catalog.filters.postgres.url");
        String user = System.getProperty("catalog.postgres.user", System.getProperty("user.name"));
        var config = new Configuration().setProperty("hibernate.connection.url", url)
            .setProperty("hibernate.connection.username", user).setProperty("hibernate.hbm2ddl.auto", "create-drop");
        var scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(Entity.class));
        for (var type : scanner.findCandidateComponents("com.innovativesoftware.domsommelier_backend")) {
            // Production mappings only; test fixtures live outside the entity packages.
            if (type.getBeanClassName().contains(".entity.")) config.addAnnotatedClass(Class.forName(type.getBeanClassName()));
        }
        config.addAnnotatedClass(com.innovativesoftware.domsommelier_backend.product_management.product.attributes.AttributeReference.class);
        factory = config.buildSessionFactory();
        jdbc = new JdbcTemplate(new DriverManagerDataSource(url, user, ""));
        String indexes = java.nio.file.Files.readString(java.nio.file.Path.of("infrastructure/db/migrations/20260912_catalog_filter_indexes.sql"));
        jdbc.execute(indexes); jdbc.execute(indexes);
        jdbc.update("INSERT INTO product_country(name) VALUES ('Франция')");
        for (String category : List.of("wine", "champagne_and_sparkling", "spirit"))
            jdbc.update("INSERT INTO product_category(name,label) VALUES (?,?)", category, category);
        jdbc.update("INSERT INTO wine_color(name) VALUES ('Белое')");
        jdbc.update("INSERT INTO spirit_category(name) VALUES ('Виски'),('Коньяк')");
        jdbc.update("INSERT INTO sparkling_wine_category(name) VALUES ('Просекко')");
        jdbc.update("INSERT INTO sugar_content(name) VALUES ('Брют')");
        jdbc.update("INSERT INTO sparkling_wine_color(name) VALUES ('Белое')");
        reference("brand", "bruni", "Bruni"); reference("region", "veneto", "Венето"); reference("region", "unused", "Не используется");
        reference("grape", "glera", "Глера"); reference("grape", "rare_grape", "Редкий сорт"); reference("vessel", "oak", "Дуб");
        reference("whisky_type", "single_malt", "Односолодовый"); reference("age_classification", "vsop", "VSOP");
        wine1 = product("wine", 1200, "{\"code\":\"bruni\",\"label\":\"Bruni\"}");
        wine2 = product("wine", 2200, null);
        jdbc.update("INSERT INTO wine(id, production_year, color, volume, extended_details) VALUES (?,2025,'Белое',0.75,?::jsonb)", wine1,
            "{\"region\":{\"code\":\"veneto\",\"label\":\"Венето\"},\"strength\":12.5,\"aging\":{\"status\":\"aged\",\"vessels\":[{\"code\":\"oak\",\"label\":\"Дуб\"}]}}");
        jdbc.update("INSERT INTO wine(id, production_year, color, volume) VALUES (?,2024,'Белое',0.75)", wine2);
        jdbc.update("INSERT INTO wine_grape(wine_id,grape) VALUES (?, 'rare_grape'), (?, 'chardonnay')", wine1, wine1);
        jdbc.update("INSERT INTO wine_store(id,location,name,city,district) VALUES (1,point(37,55),'Test','moscow','Test')");
        jdbc.update("INSERT INTO product_stock(id,product_id,wine_store_id,quantity) VALUES (?,?,1,2)", UUID.randomUUID(), wine1);
        sparkling1 = product("champagne_and_sparkling", 1500, null);
        jdbc.update("INSERT INTO sparkling_wine(id,subcategory,sugar_content,color,volume,extended_details) VALUES (?,'Просекко','Брют','Белое',0.75,?::jsonb)", sparkling1,
            "{\"strength\":11,\"grapeComposition\":[{\"grape\":{\"code\":\"glera\",\"label\":\"Глера\"},\"percent\":100}],\"productionYear\":2025,\"vintageStatus\":\"vintage\"}");
        whisky1 = product("spirit", 5000, null); cognac1 = product("spirit", 6000, null);
        jdbc.update("INSERT INTO spirit(id,subcategory,strength,volume,extended_details) VALUES (?,'Виски',40,0.7,?::jsonb)", whisky1,
            "{\"whiskyDetails\":{\"whiskyType\":{\"code\":\"single_malt\",\"label\":\"Односолодовый\"},\"ageStatementYears\":12,\"ageStatementStatus\":\"stated\"}}");
        jdbc.update("INSERT INTO spirit(id,subcategory,strength,volume,extended_details) VALUES (?,'Коньяк',40,0.7,?::jsonb)", cognac1,
            "{\"cognacDetails\":{\"ageClassification\":{\"code\":\"vsop\",\"label\":\"VSOP\"},\"ageStatementYears\":4}}");
    }
    UUID product(String category, int price, String brand) {
        UUID id = UUID.randomUUID();
        jdbc.update("INSERT INTO product(id,article,name,initial_price,price,country_name,category_name,brand) VALUES (?,?,?, ?,?,'Франция',?,?::jsonb)", id, id.toString(), "Test", price, price, category, brand);
        return id;
    }
    void reference(String kind, String code, String label) {
        jdbc.update("INSERT INTO catalog_attribute_reference(id,kind,code,label) VALUES (?,?,?,?)", kind+":"+code, kind, code, label);
    }
    @AfterAll void close() { if (factory != null) factory.close(); }
    <T> List<T> query(Class<T> type, BaseSpecification<T> spec, Map<String,Object> params) {
        try (var em = factory.createEntityManager()) {
            var cb = em.getCriteriaBuilder(); var q = cb.createQuery(type); var root = q.from(type);
            q.select(root).where(spec.byFilter(params).toPredicate(root, q, cb));
            return em.createQuery(q).getResultList();
        }
    }
    @Test void scalarAndNumericFiltersIncludeLegacyProductsWhenUnset() {
        var spec = new WineSpecification();
        assertEquals(2, query(Wine.class, spec, Map.of()).size());
        assertEquals(1, query(Wine.class, spec, Map.of("brand", List.of("bruni"), "region", List.of("veneto"))).size());
        assertEquals(1, query(Wine.class, spec, Map.of("strength", Arrays.asList(null, 13))).size());
        assertEquals(0, query(Wine.class, spec, Map.of("strength", List.of(13, 14))).size());
        assertThrows(org.springframework.web.server.ResponseStatusException.class,
            () -> query(Wine.class, spec, Map.of("strength", List.of(14, 10))));
    }
    @Test void arrayFilteringAndFacetsUseCodesAndCountEachProductOnce() {
        var spec = new ShampaigneSparklingSpecification();
        assertEquals(1, query(SparklingWine.class, spec, Map.of("grapeComposition", List.of("glera", "unknown"))).size());
        assertEquals(0, query(SparklingWine.class, spec, Map.of("grapeComposition", List.of("x\" || true"))).size());
        assertEquals(1, query(Wine.class, new WineSpecification(), Map.of("agingVessel", List.of("oak"))).size());
        try (var em = factory.createEntityManager()) {
            var facets = new FacetCounter(em).count(SparklingWine.class, spec, Map.of("grapeComposition", List.of("glera")), List.of("color"));
            assertEquals(1, facets.total());
            assertEquals(1L, facets.options().get("grapeComposition").get("glera"));
        }
    }
    @Test void subtypeSwitchDropsHiddenConditionsAndKeepsOtherTypesSelectable() {
        var spec = new SpiritSpecification();
        assertEquals(1, query(Spirit.class, spec, Map.of("subcategory", List.of("Виски"), "whiskyAge", List.of(10, 15))).size());
        assertEquals(1, query(Spirit.class, spec, Map.of("subcategory", List.of("Коньяк"), "whiskyAge", List.of(10, 15))).size());
        assertEquals(2, query(Spirit.class, spec, Map.of("subcategory", List.of("Виски", "Коньяк"), "whiskyAge", List.of(10, 15))).size());
        try (var em = factory.createEntityManager()) {
            var facets = new FacetCounter(em).count(Spirit.class, spec,
                Map.of("subcategory", List.of("Виски"), "whiskyType", List.of("single_malt")), List.of("subcategory"));
            assertEquals(1L, facets.options().get("subcategory").get("Коньяк"));
            assertEquals(1L, facets.options().get("whiskyType").get("single_malt"));
        }
    }
    @Test void legacyGrapesAndCityAvailabilityRemainCompatible() {
        var spec = new WineSpecification();
        assertEquals(1, query(Wine.class, spec, Map.of("grape", List.of("rare_grape", "chardonnay"))).size());
        assertEquals(1, query(Wine.class, spec, Map.of("grape", List.of("Шардоне"))).size());
        assertEquals(1, query(Wine.class, spec, Map.of("city", "moscow", "brand", List.of("bruni"))).size());
        assertEquals(0, query(Wine.class, spec, Map.of("city", "other", "brand", List.of("bruni"))).size());
        try (var em = factory.createEntityManager()) {
            var facets = new FacetCounter(em).count(Wine.class, spec, Map.of("city", "moscow"), List.of("grape"));
            assertEquals(1L, facets.total());
            assertEquals(1L, facets.options().get("grape").get("rare_grape"));
            assertEquals(1L, facets.options().get("grape").get("chardonnay"));
            assertEquals(1L, facets.options().get("grape").get("Шардоне"));
        }
    }
    @Test void configurationUsesAssignedReferencesAndRealRangeBounds() {
        var config = new CatalogFilterConfigService(jdbc, List.of());
        var wine = config.enrich(ProductCategoryEnum.wine, List.of());
        assertEquals(2024d, ((RangeFilterDto) wine.get("year")).getMin());
        var grape = (MultiSelectFilterDto) wine.get("grape");
        assertTrue(Arrays.stream(grape.getOptions()).anyMatch(o -> o.getValue().equals("rare_grape") && o.getLabel().equals("Редкий сорт")));
        var region = (MultiSelectFilterDto) wine.get("region");
        assertEquals(1, region.getOptions().length);
        assertEquals("veneto", region.getOptions()[0].getValue());
        assertEquals("Венето", region.getOptions()[0].getLabel());
        assertEquals("value", region.getSelectionMode());
        assertEquals(12.5, ((RangeFilterDto) wine.get("strength")).getMin());
        assertFalse(wine.containsKey("appellation"));
        var sparkling = config.enrich(ProductCategoryEnum.champagne_and_sparkling, List.of());
        assertEquals("glera", ((MultiSelectFilterDto) sparkling.get("grapeComposition")).getOptions()[0].getValue());
        var spirit = config.enrich(ProductCategoryEnum.spirit, List.of());
        assertEquals("whisky", spirit.get("whiskyAge").getSubtype());
        assertEquals("cognac", spirit.get("cognacClassification").getSubtype());
    }
}
