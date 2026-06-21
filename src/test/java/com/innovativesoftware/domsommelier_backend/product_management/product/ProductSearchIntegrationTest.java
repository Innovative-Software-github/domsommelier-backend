package com.innovativesoftware.domsommelier_backend.product_management.product;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Product search integration tests")
class ProductSearchIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET /search?q= — возвращает Page с карточками продуктов")
    void searchByQuery_returnsPagedProductCards() throws Exception {
        mockMvc.perform(get("/api/v1/products/search")
                        .param("q", "Louis")
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").isNumber())
                .andExpect(jsonPath("$.totalPages").isNumber())
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.size").value(5));
    }

    @Test
    @DisplayName("GET /search без q — пустая страница")
    void searchWithoutQuery_returnsEmptyPage() throws Exception {
        mockMvc.perform(get("/api/v1/products/search"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    @DisplayName("GET /search?q= — несуществующий запрос, пустой content")
    void searchNoResults_returnsEmptyContent() throws Exception {
        mockMvc.perform(get("/api/v1/products/search")
                        .param("q", "xyznotfoundxyz12345"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content").isEmpty());
    }

    @Test
    @DisplayName("GET /search?q= — кириллица в query-параметре")
    void searchCyrillicQuery_returnsOk() throws Exception {
        mockMvc.perform(get("/api/v1/products/search")
                        .param("q", "вино")
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @DisplayName("GET /search — пагинация page=1")
    void searchPagination_returnsSecondPage() throws Exception {
        mockMvc.perform(get("/api/v1/products/search")
                        .param("q", "а")
                        .param("page", "1")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value(1))
                .andExpect(jsonPath("$.size").value(2));
    }

    @Test
    @DisplayName("GET /search?city= — фильтр по городу")
    void searchWithCity_returnsOk() throws Exception {
        mockMvc.perform(get("/api/v1/products/search")
                        .param("q", "вино")
                        .param("city", "moscow")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }
}
