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
@DisplayName("Тесты для фильтра крепких напитков")
public class FilterSpiritTests {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
    }

    private List<Map<String, Object>> filterProducts(Map<String, Object> params) throws Exception {
        String json = objectMapper.writeValueAsString(params);

        MvcResult mvcResult = this.mockMvc.perform(
                        post("/api/v1/products/filter")
                                .param("category", String.valueOf(ProductCategoryEnum.spirit))
                                .param("page", "0")
                                .param("size", "10")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString();

        return objectMapper.convertValue(
                objectMapper.readTree(responseBody).get("content"),
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
                    double price = (double) product.get("price");
                    assertTrue(price >= 1000 &&  price <= 5000);
                });
    }

    @DisplayName("Тестирование поля страны")
    @Test
    void testFilterCountry() throws Exception {
        for (String country : List.of("Россия", "Шотландия")) {
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
        for (BigDecimal volume : List.of(BigDecimal.valueOf(0.70), BigDecimal.valueOf(0.50))) {
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
                            assertEquals(0, volume.compareTo(VolumeStrengthUtils.parseVolume(String.valueOf(details.get("volume")))));
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    });
        }
    }

    @DisplayName("Тестирование категории крепких напитков")
    @Test
    void testFilterSubcategory() throws Exception {
        for (String feature : List.of("Коньяк", "Виски")) {
            Map<String, Object> params = new HashMap<>();
            params.put("subcategory", List.of(feature));
            List<Map<String, Object>> resultList = filterProducts(params);

            assertFalse(resultList.isEmpty());
            resultList.forEach(
                    product -> {
                        try {
                            ProductDTO productDTO = getProductById(UUID.fromString((String) product.get("id")));
                            assertNotNull(productDTO);
                            Map<String, Object> details = (Map<String, Object>) productDTO.getDetails();
                            assertTrue(details.containsKey("category"));
                            String snackCategory = String.valueOf(details.get("category"));
                            assertEquals(feature, snackCategory);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    });
        }
    }

    @DisplayName("Тестирование крепости напитка")
    @Test
    void testFilterStrength() throws Exception {
        for (BigDecimal strength : List.of(BigDecimal.valueOf(40.00))) {
            Map<String, Object> params = new HashMap<>();
            params.put("strength", List.of(strength));
            List<Map<String, Object>> resultList = filterProducts(params);

            assertFalse(resultList.isEmpty());
            resultList.forEach(
                    product -> {
                        try {
                            ProductDTO productDTO = getProductById(UUID.fromString((String) product.get("id")));
                            assertNotNull(productDTO);
                            Map<String, Object> details = (Map<String, Object>) productDTO.getDetails();
                            assertTrue(details.containsKey("strength"));
                            String strength1 = String.valueOf(details.get("strength"));
                            assertEquals(0, strength.compareTo(VolumeStrengthUtils.parseStrength(strength1)));
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    });
        }
    }

    @DisplayName("Тестирование поля производителя")
    @Test
    void testFilterProducer() throws Exception {
        for (String producer : List.of("Johnnie Walker", "Beluga")) {
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

    @DisplayName("Тестирование поля фич")
    @Test
    void testFilterFeatures() throws Exception {
        for (String feature : List.of("gift_set")) {
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
}
