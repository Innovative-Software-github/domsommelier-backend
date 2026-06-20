package com.innovativesoftware.domsommelier_backend.event_management.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innovativesoftware.domsommelier_backend.event_management.event.entity.Event;
import com.innovativesoftware.domsommelier_backend.event_management.event.entity.EventPhoto;
import com.innovativesoftware.domsommelier_backend.event_management.event.repository.EventPhotoRepository;
import com.innovativesoftware.domsommelier_backend.event_management.event.repository.EventRepository;
import com.innovativesoftware.domsommelier_backend.file_management.model.FileService;
import com.innovativesoftware.domsommelier_backend.infrastructure.BucketRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Event admin integration tests")
class EventAdminIntegrationTest {

    private static final String EVENT_BODY = """
            {
              "type": "degustation",
              "price": 1500,
              "datetime": "2026-07-01T18:00:00Z",
              "title": "Тестовое мероприятие",
              "description": "Описание",
              "wineStoreId": 1
            }
            """;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventPhotoRepository eventPhotoRepository;

    @MockBean
    private FileService fileService;

    @BeforeEach
    void setUpFileService() {
        doNothing().when(fileService).deleteFile(anyString(), anyString());
        when(fileService.fileUrl(anyString(), anyString(), anyString()))
                .thenAnswer(invocation -> "http://test/" + invocation.getArgument(2));
    }

    private UUID createEventAndReturnId(String body) throws Exception {
        String createResponse = mockMvc.perform(post("/api/v1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return UUID.fromString(objectMapper.readTree(createResponse).get("id").asText());
    }

    @Test
    @DisplayName("GET /api/v1/events/filter без auth → 200")
    void getFilteredEvents_withoutAuth_returns200() throws Exception {
        mockMvc.perform(get("/api/v1/events/filter"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @DisplayName("POST /api/v1/events без auth → 401")
    void createEvent_withoutAuth_returns401() throws Exception {
        mockMvc.perform(post("/api/v1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(EVENT_BODY))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("POST /api/v1/events как USER → 403")
    void createEvent_asUser_returns403() throws Exception {
        mockMvc.perform(post("/api/v1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(EVENT_BODY))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /api/v1/events как ADMIN → 200")
    void createEvent_asAdmin_returns200() throws Exception {
        mockMvc.perform(post("/api/v1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(EVENT_BODY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("Тестовое мероприятие"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("PUT с прошедшей датой как ADMIN → 200")
    void updateEvent_withPastDate_asAdmin_returns200() throws Exception {
        UUID id = createEventAndReturnId(EVENT_BODY);

        mockMvc.perform(put("/api/v1/events/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "type": "wineCasino",
                                  "price": 2000,
                                  "datetime": "2020-01-01T18:00:00Z",
                                  "title": "Прошедшее мероприятие",
                                  "wineStoreId": 1
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Прошедшее мероприятие"))
                .andExpect(jsonPath("$.type").value("wineCasino"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("DELETE /api/v1/events/{id} с привязанными фото → 204")
    void deleteEvent_withPhotos_asAdmin_returns204() throws Exception {
        UUID id = createEventAndReturnId(EVENT_BODY);
        Event event = eventRepository.findById(id).orElseThrow();

        EventPhoto photo = EventPhoto.builder()
                .bucket(BucketRegistry.Bucket.EVENT.getName())
                .name("test-photo.png")
                .description("test")
                .url("http://test/test-photo.png")
                .event(event)
                .build();
        eventPhotoRepository.save(photo);
        assertThat(eventPhotoRepository.findByEvent_Id(id)).hasSize(1);

        mockMvc.perform(delete("/api/v1/events/" + id))
                .andExpect(status().isNoContent());

        assertThat(eventRepository.findById(id)).isEmpty();
        assertThat(eventPhotoRepository.findByEvent_Id(id)).isEmpty();
    }

    @Test
    @DisplayName("POST /events/files/upload без auth → 401")
    void uploadPhoto_withoutAuth_returns401() throws Exception {
        mockMvc.perform(post("/events/files/upload")
                        .param("eventId", UUID.randomUUID().toString())
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isUnauthorized());
    }
}
