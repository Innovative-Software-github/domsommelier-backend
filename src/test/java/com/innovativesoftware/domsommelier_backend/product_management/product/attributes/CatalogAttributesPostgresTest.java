package com.innovativesoftware.domsommelier_backend.product_management.product.attributes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.attributes.CatalogAttributes.*;
import jakarta.persistence.*;
import org.hibernate.SessionFactory;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.cfg.Configuration;
import org.hibernate.type.SqlTypes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.DriverManager;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

/** Uses ONLY a disposable database selected explicitly by the test runner. */
@EnabledIfSystemProperty(named = "catalog.postgres.url", matches = ".+")
class CatalogAttributesPostgresTest {
    @Entity(name="AttributeProductFixture") @Table(name="product")
    static class ProductRow {
        @Id UUID id;
        @JdbcTypeCode(SqlTypes.JSON) @Column(columnDefinition="jsonb") Reference brand;
        @JdbcTypeCode(SqlTypes.JSON) @Column(columnDefinition="jsonb") Packaging packaging;
    }
    @Entity(name="AttributeWineFixture") @Table(name="wine")
    static class WineRow {
        @Id UUID id;
        @JdbcTypeCode(SqlTypes.JSON) @Column(name="extended_details", columnDefinition="jsonb") WineAttributes details;
    }
    @Entity(name="AttributeSparklingFixture") @Table(name="sparkling_wine")
    static class SparklingRow {
        @Id UUID id;
        @JdbcTypeCode(SqlTypes.JSON) @Column(name="extended_details", columnDefinition="jsonb") SparklingAttributes details;
    }
    @Entity(name="AttributeSpiritFixture") @Table(name="spirit")
    static class SpiritRow {
        @Id UUID id;
        @JdbcTypeCode(SqlTypes.JSON) @Column(name="extended_details", columnDefinition="jsonb") SpiritAttributes details;
    }

    @Test void migrationIsRepeatableAndHibernateRoundTripsTypedJsonb() throws Exception {
        String url = System.getProperty("catalog.postgres.url");
        String user = System.getProperty("catalog.postgres.user", System.getProperty("user.name"));
        UUID oldId = UUID.randomUUID();
        try (var connection = DriverManager.getConnection(url, user, ""); var statement = connection.createStatement()) {
            for (String table : new String[]{"product", "wine", "sparkling_wine", "spirit"}) statement.execute("CREATE TABLE " + table + " (id uuid PRIMARY KEY)");
            statement.execute("INSERT INTO product(id) VALUES ('" + oldId + "')");
            String migration = Files.readString(Path.of("infrastructure/db/migrations/20260912_product_attributes.sql"));
            statement.execute(migration); statement.execute(migration);
            try (var row = statement.executeQuery("SELECT brand, packaging FROM product WHERE id = '" + oldId + "'")) {
                assertTrue(row.next()); assertNull(row.getString(1)); assertNull(row.getString(2));
            }
        }
        ObjectMapper json = new ObjectMapper();
        var wine = json.readValue("{\"strength\":12.5,\"region\":{\"code\":\"marlborough\",\"label\":\"Мальборо\"},\"grapeComposition\":[{\"grape\":{\"code\":\"sauvignon_blanc\",\"label\":\"Совиньон блан\"},\"percent\":100}],\"grapeCompositionComplete\":true}", WineAttributes.class);
        var sparkling = json.readValue("{\"vintageStatus\":\"unknown\",\"sparklingMethod\":{\"code\":\"charmat\",\"label\":\"Шарма\"}}", SparklingAttributes.class);
        var spirit = json.readValue("{\"whiskyDetails\":{\"ageStatementStatus\":\"unknown\",\"componentCount\":3}}", SpiritAttributes.class);
        try (SessionFactory factory = new Configuration()
            .addAnnotatedClass(ProductRow.class).addAnnotatedClass(WineRow.class)
            .addAnnotatedClass(SparklingRow.class).addAnnotatedClass(SpiritRow.class)
            .setProperty("hibernate.connection.url", url).setProperty("hibernate.connection.username", user)
            .setProperty("hibernate.connection.driver_class", "org.postgresql.Driver")
            .setProperty("hibernate.hbm2ddl.auto", "validate").buildSessionFactory()) {
            UUID id = UUID.randomUUID();
            try (var session = factory.openSession()) {
                var tx = session.beginTransaction();
                ProductRow product = new ProductRow(); product.id = id; product.brand = new Reference("camus", "Camus"); product.packaging = new Packaging(true, null); session.persist(product);
                WineRow w = new WineRow(); w.id = id; w.details = wine; session.persist(w);
                SparklingRow s = new SparklingRow(); s.id = id; s.details = sparkling; session.persist(s);
                SpiritRow r = new SpiritRow(); r.id = id; r.details = spirit; session.persist(r);
                tx.commit();
            }
            try (var session = factory.openSession()) {
                assertEquals(wine, session.find(WineRow.class, id).details);
                assertEquals(sparkling, session.find(SparklingRow.class, id).details);
                assertEquals(spirit, session.find(SpiritRow.class, id).details);
                assertEquals("Camus", session.find(ProductRow.class, id).brand.label());
                var tx = session.beginTransaction(); session.find(WineRow.class, id).details = null; tx.commit();
            }
            try (var session = factory.openSession()) { assertNull(session.find(WineRow.class, id).details); }
        }
    }
}
