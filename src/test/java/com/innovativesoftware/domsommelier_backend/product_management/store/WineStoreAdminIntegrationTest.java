package com.innovativesoftware.domsommelier_backend.product_management.store;

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
@DisplayName("Wine store admin integration tests")
class WineStoreAdminIntegrationTest {

    private static final String WINE_STORE_BODY = """
            {
              "name":"Test Store",
              "city":"Москва",
              "district":"Центр",
              "longitude":37.6,
              "latitude":55.7
            }
            """;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private long createWineStoreAndReturnId(String body) throws Exception {
        String createResponse = mockMvc.perform(post("/api/v1/wine-stores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return objectMapper.readTree(createResponse).get("id").asLong();
    }

    @Test
    @DisplayName("GET /api/v1/wine-stores без auth → 200")
    void getWineStores_withoutAuth_returns200() throws Exception {
        mockMvc.perform(get("/api/v1/wine-stores"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @DisplayName("POST /api/v1/wine-stores без auth → 401")
    void createWineStore_withoutAuth_returns401() throws Exception {
        mockMvc.perform(post("/api/v1/wine-stores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(WINE_STORE_BODY))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("POST /api/v1/wine-stores как USER → 403")
    void createWineStore_asUser_returns403() throws Exception {
        mockMvc.perform(post("/api/v1/wine-stores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(WINE_STORE_BODY))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("PUT /api/v1/wine-stores/{id} как USER → 403")
    void updateWineStore_asUser_returns403() throws Exception {
        mockMvc.perform(put("/api/v1/wine-stores/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(WINE_STORE_BODY))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("DELETE /api/v1/wine-stores/{id} как USER → 403")
    void deleteWineStore_asUser_returns403() throws Exception {
        mockMvc.perform(delete("/api/v1/wine-stores/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /api/v1/wine-stores как ADMIN → 201")
    void createWineStore_asAdmin_returns201() throws Exception {
        mockMvc.perform(post("/api/v1/wine-stores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name":"Admin Test Store",
                                  "city":"Москва",
                                  "district":"Центр",
                                  "longitude":37.61,
                                  "latitude":55.76
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.city").value("москва"))
                .andExpect(jsonPath("$.location.latitude").value(55.76));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("PUT /api/v1/wine-stores/{id} как ADMIN → 200")
    void updateWineStore_asAdmin_returns200() throws Exception {
        long id = createWineStoreAndReturnId("""
                {
                  "name":"Store To Update",
                  "city":"Пермь",
                  "district":"Центр",
                  "longitude":56.23,
                  "latitude":58.01
                }
                """);

        mockMvc.perform(put("/api/v1/wine-stores/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name":"Updated Store",
                                  "city":"Пермь",
                                  "district":"край",
                                  "longitude":56.24,
                                  "latitude":58.02
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Store"))
                .andExpect(jsonPath("$.district").value("край"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("DELETE /api/v1/wine-stores/{id} без связей → 204")
    void deleteWineStore_asAdmin_returns204() throws Exception {
        long id = createWineStoreAndReturnId("""
                {
                  "name":"Store To Delete",
                  "city":"москва",
                  "district":"тест",
                  "longitude":37.6,
                  "latitude":55.7
                }
                """);

        mockMvc.perform(delete("/api/v1/wine-stores/" + id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/wine-stores/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("DELETE /api/v1/wine-stores/{id} с привязанными клиентами → 409")
    void deleteWineStore_withLinkedCustomers_returns409() throws Exception {
        mockMvc.perform(delete("/api/v1/wine-stores/1"))
                .andExpect(status().isConflict());
    }
}
