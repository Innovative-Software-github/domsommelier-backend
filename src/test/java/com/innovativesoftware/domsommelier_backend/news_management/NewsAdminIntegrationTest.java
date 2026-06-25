package com.innovativesoftware.domsommelier_backend.news_management;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("News CRUD integration tests")
class NewsAdminIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Полный цикл: создание → чтение → список → обновление → удаление → 404")
    void newsCrudLifecycle() throws Exception {
        String createBody = """
                {"title":"Дегустация недели","description":"Описание","reference":"https://example.com"}
                """;

        String created = mockMvc.perform(post("/api/v1/news")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("Дегустация недели"))
                .andExpect(jsonPath("$.coverUrl").doesNotExist())
                .andExpect(jsonPath("$.publishedAt").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String id = objectMapper.readTree(created).get("id").asText();

        mockMvc.perform(get("/api/v1/news/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Дегустация недели"));

        mockMvc.perform(get("/api/v1/news"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());

        String updateBody = """
                {"title":"Обновлённый заголовок","description":"Новое описание"}
                """;
        mockMvc.perform(put("/api/v1/news/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Обновлённый заголовок"));

        mockMvc.perform(delete("/api/v1/news/{id}", id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/news/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Создание без заголовка → 400")
    void createWithoutTitle_returnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/v1/news")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"description\":\"Без заголовка\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Несуществующая новость → 404")
    void getMissing_returnsNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/news/{id}", "00000000-0000-0000-0000-000000000000"))
                .andExpect(status().isNotFound());
    }
}
