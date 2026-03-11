package com.innovativesoftware.domsommelier_backend.filter_tests;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.innovativesoftware.domsommelier_backend.product_management.product.enums.ProductCategoryEnum;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.ProductDTO;
import com.innovativesoftware.domsommelier_backend.product_management.product.util.VolumeStrengthUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@DisplayName("Тесты для фильтра вина")
public class FilterWineTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        /*
        jdbcTemplate.update("DELETE FROM gourmet_pairing");
        jdbcTemplate.update("DELETE FROM gourmet_product");
        jdbcTemplate.update("DELETE FROM wine_feature");
        jdbcTemplate.update("DELETE FROM wine_grape");
        jdbcTemplate.update("DELETE FROM wine");

        jdbcTemplate.update("DELETE FROM product");

        jdbcTemplate.update("MERGE INTO product_country (name) KEY(name) VALUES (?)", "Италия");
        jdbcTemplate.update("MERGE INTO wine_color (name) KEY(name) VALUES (?)", "Красное");
        jdbcTemplate.update("MERGE INTO wine_type (name) KEY(name) VALUES (?)", "Полусладкое");

        UUID wineId = UUID.randomUUID();
        jdbcTemplate.update(
                "INSERT INTO product (id, article, name, initial_price, price, category_name, country_name, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                wineId, "WINE210", "Santa Cristina Toscana", 3600, 3700, "wine", "Италия", Timestamp.valueOf("2024-05-26 13:30:00")
        );
        jdbcTemplate.update(
                "INSERT INTO wine (id, production_year, color, type, producer, volume) VALUES (?, ?, ?, ?, ?, ?)",
                wineId, 2020, "Красное", "Полусладкое", "Santa Cristina", 0.75
        );
        jdbcTemplate.update(
                "INSERT INTO wine_grape (wine_id, grape) VALUES (?, ?)",
                wineId, "sangiovese"
        );
        jdbcTemplate.update(
                "INSERT INTO wine_feature (wine_id, feature) VALUES (?, ?)",
                wineId, "gift_wrapping"
        );*/
    }

    private List<Map<String, Object>> filterProducts(Map<String, Object> params) throws Exception {
        String json = objectMapper.writeValueAsString(params);

        MvcResult mvcResult = this.mockMvc.perform(
                        post("/api/v1/products/filter")
                                .param("category", String.valueOf(ProductCategoryEnum.wine))
                                .param("page", "0")
                                .param("size", "10")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString();

        return objectMapper.readValue(
                responseBody,
                new TypeReference<>() {}
        );
    }

    private ProductDTO getProductById(UUID id) throws Exception {
        if (id != null) {
            String responseBody = this.mockMvc.perform(
                            get("/api/v1/products").param("id", id.toString())
                    )
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();
            if (!responseBody.isEmpty()) {
                return objectMapper.readValue(responseBody, ProductDTO.class);
            }
        }
        return null;
    }

    @DisplayName("Тестирование поля цен")
    @Test
    void testFilterPrice() throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("price", List.of(1000, 5000));
        List<Map<String, Object>> resultList = filterProducts(params);

        assertFalse(resultList.isEmpty());
        resultList.forEach(
                product -> {
                    assertInstanceOf(Double.class, product.get("price"));
                    assertTrue((Double) product.get("price") >= 1000 && (Double) product.get("price") <= 5000);
                });
    }

    @DisplayName("Тестирование поля цвета")
    @Test
    void testFilterColor() throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("color", List.of("Красное"));
        List<Map<String, Object>> resultList = filterProducts(params);

        assertFalse(resultList.isEmpty());
        resultList.forEach(
                product -> {
                    try {
                        ProductDTO productDTO = getProductById(UUID.fromString((String) product.get("id")));
                        assertNotNull(productDTO);
                        Map<String, Object> details = (Map<String, Object>) productDTO.getDetails();
                        assertEquals("Красное", details.get("color"));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    @DisplayName("Тестирование поля типа")
    @Test
    void testFilterType() throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("type", List.of("Полусладкое"));
        List<Map<String, Object>> resultList = filterProducts(params);

        assertFalse(resultList.isEmpty());
        resultList.forEach(
                product -> {
                    try {
                        ProductDTO productDTO = getProductById(UUID.fromString((String) product.get("id")));
                        assertNotNull(productDTO);
                        Map<String, Object> details = (Map<String, Object>) productDTO.getDetails();
                        assertEquals("Полусладкое", details.get("type"));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    @DisplayName("Тестирование поля страны")
    @Test
    void testFilterCountry() throws Exception {
        for (String country : List.of("Италия", "Франция")) {
            Map<String, Object> params = new HashMap<>();
            params.put("countries", List.of(country));
            List<Map<String, Object>> resultList = filterProducts(params);

            assertFalse(resultList.isEmpty());
            resultList.forEach(
                    product -> {
                        try {
                            assertEquals(country, String.valueOf(product.get("productCountry")));
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    });
        }
    }

    @DisplayName("Тестирование поля объёма")
    @Test
    void testFilterVolume() throws Exception {
        for (BigDecimal volume : List.of(BigDecimal.valueOf(0.75), BigDecimal.valueOf(1.00))) {
            Map<String, Object> params = new HashMap<>();
            params.put("volume", List.of(volume));
            List<Map<String, Object>> resultList = filterProducts(params);

            assertFalse(resultList.isEmpty());
            resultList.forEach(
                    product -> {
                        try {
                            ProductDTO productDTO = getProductById(UUID.fromString((String) product.get("id")));
                            assertNotNull(productDTO);
                            Map<String, Object> details = (Map<String, Object>) productDTO.getDetails();
                            //assertEquals(volume, VolumeStrengthUtils.parseVolume(String.valueOf(details.get("volume"))));
                            assertEquals(0, volume.compareTo(VolumeStrengthUtils.parseVolume(String.valueOf(details.get("volume")))));
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    });
        }
    }

    @DisplayName("Тестирование поля год выпуска")
    @Test
    void testFilterYear() throws Exception {
        for (List<Integer> years : List.of(List.of(2020, 2021), List.of(2020, 2024))) {
            Map<String, Object> params = new HashMap<>();
            params.put("year", years);
            List<Map<String, Object>> resultList = filterProducts(params);

            assertFalse(resultList.isEmpty());
            resultList.forEach(
                    product -> {
                        try {
                            ProductDTO productDTO = getProductById(UUID.fromString((String) product.get("id")));
                            assertNotNull(productDTO);
                            Map<String, Object> details = (Map<String, Object>) productDTO.getDetails();
                            Integer productionYear = (Integer) details.get("productionYear");
                            assertTrue(productionYear >= years.get(0) && productionYear <= years.get(1));
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    });
        }
    }

    @DisplayName("Тестирование поля фич")
    @Test
    void testFilterFeatures() throws Exception {
        for (String feature : List.of("gift_wrapping")) {
            Map<String, Object> params = new HashMap<>();
            params.put("features", List.of(feature));
            List<Map<String, Object>> resultList = filterProducts(params);

            assertFalse(resultList.isEmpty());
            resultList.forEach(
                    product -> {
                        try {
                            ProductDTO productDTO = getProductById(UUID.fromString((String) product.get("id")));
                            assertNotNull(productDTO);
                            Map<String, Object> details = (Map<String, Object>) productDTO.getDetails();
                            assertTrue(details.containsKey("features"));
                            List<String> features = (List<String>) details.get("features");
                            assertTrue(features.contains(feature));
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    });
        }
    }

    @DisplayName("Тестирование поля производителя")
    @Test
    void testFilterProducer() throws Exception {
        for (String producer : List.of("Abrau-Durso", "Chateau Tamagne")) {
            Map<String, Object> params = new HashMap<>();
            params.put("producer", List.of(producer));
            List<Map<String, Object>> resultList = filterProducts(params);

            assertFalse(resultList.isEmpty());
            resultList.forEach(
                    product -> {
                        try {
                            ProductDTO productDTO = getProductById(UUID.fromString((String) product.get("id")));
                            assertNotNull(productDTO);
                            Map<String, Object> details = (Map<String, Object>) productDTO.getDetails();
                            assertEquals(producer, details.get("producer"));
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    });
        }
    }
}
