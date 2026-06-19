package com.innovativesoftware.domsommelier_backend.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Admin authorization integration tests")
class AdminAuthorizationIntegrationTest {

    private static final String NEWS_BODY = """
            {"title":"Test","description":"Body","reference":"ref"}
            """;

    private static final String WINE_STORE_BODY = """
            {
              "name":"Test Store",
              "city":"москва",
              "district":"центр",
              "longitude":37.6,
              "latitude":55.7
            }
            """;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("POST /api/v1/news без auth → 401")
    void createNews_withoutAuth_returns401() throws Exception {
        mockMvc.perform(post("/api/v1/news")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(NEWS_BODY))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("POST /api/v1/news как USER → 403")
    void createNews_asUser_returns403() throws Exception {
        mockMvc.perform(post("/api/v1/news")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(NEWS_BODY))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /api/v1/news как ADMIN → не 403")
    void createNews_asAdmin_isNotForbidden() throws Exception {
        mockMvc.perform(post("/api/v1/news")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(NEWS_BODY))
                .andExpect(status().is(not(403)));
    }

    @Test
    @DisplayName("GET /api/v1/news без auth → 200")
    void getNews_withoutAuth_returns200() throws Exception {
        mockMvc.perform(get("/api/v1/news"))
                .andExpect(status().isOk());
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
}
