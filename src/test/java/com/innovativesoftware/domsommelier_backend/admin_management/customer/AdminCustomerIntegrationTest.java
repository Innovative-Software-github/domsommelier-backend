package com.innovativesoftware.domsommelier_backend.admin_management.customer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Admin customers integration tests")
class AdminCustomerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET /api/v1/admin/customers без auth → 401")
    void getCustomers_withoutAuth_returns401() throws Exception {
        mockMvc.perform(get("/api/v1/admin/customers"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("GET /api/v1/admin/customers как USER → 403")
    void getCustomers_asUser_returns403() throws Exception {
        mockMvc.perform(get("/api/v1/admin/customers"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /api/v1/admin/customers как ADMIN → 200")
    void getCustomers_asAdmin_returns200() throws Exception {
        mockMvc.perform(get("/api/v1/admin/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /api/v1/admin/customers/{id} для несуществующего → 404")
    void getCustomer_unknownId_returns404() throws Exception {
        mockMvc.perform(get("/api/v1/admin/customers/00000000-0000-0000-0000-000000000001"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /api/v1/admin/customers/{id}/orders для несуществующего → 404")
    void getCustomerOrders_unknownCustomer_returns404() throws Exception {
        mockMvc.perform(get("/api/v1/admin/customers/00000000-0000-0000-0000-000000000001/orders"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /api/v1/admin/customers/{id}/orders → массив заказов")
    void getCustomerOrders_existingCustomer_returns200() throws Exception {
        mockMvc.perform(get("/api/v1/admin/customers/11111111-e5f6-4789-8012-000000000001/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }
}
