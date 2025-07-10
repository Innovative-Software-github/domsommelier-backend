package com.innovativesoftware.domsommelier_backend.event_management.event.controller;

import com.innovativesoftware.domsommelier_backend.event_management.event.model.EventPhotoDTO;
import com.innovativesoftware.domsommelier_backend.event_management.event.service.EventPhotoOperationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/events/files")
@Tag(name = "Event Photo CRUD", description = "CRUD для фото мероприятий в MinIO")
@RequiredArgsConstructor
public class EventPhotoController {

    private final EventPhotoOperationService service;

    @Operation(summary="Загрузка фото к мероприятию")
    @PostMapping(path = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public List<EventPhotoDTO> uploadPhoto(
            @RequestPart("files") MultipartFile[] files,
            @RequestPart("description") String description,
            @RequestParam String eventId
    ) {
        return service.uploadFilesWithRef(files, "event", eventId, description);
    }

    @Operation(summary="Получить список фото для мероприятия")
    @GetMapping("/list")
    public List<EventPhotoDTO> getPhotosByEvent(@RequestParam String eventId) {
        return service.getPhotosByEvent(UUID.fromString(eventId));
    }

    @Operation(summary="Получить одно фото (метаданные)")
    @GetMapping("/{photoId}/meta")
    public EventPhotoDTO getPhotoInfo(@PathVariable UUID photoId) {
        return service.getPhotoInfo(photoId);
    }

    @Operation(summary="Скачать фото по имени файла")
    @GetMapping("")
    public ResponseEntity<byte[]> downloadPhoto(@RequestParam("file") String fileName) {
        byte[] bytes = service.getPhotoBytes("event", fileName);

        // Определяем mimetype по расширению (можно сделать умнее)
        String contentType = "application/octet-stream";
        if (fileName.toLowerCase().endsWith(".jpg") || fileName.toLowerCase().endsWith(".jpeg")) {
            contentType = "image/jpeg";
        } else if (fileName.toLowerCase().endsWith(".png")) {
            contentType = "image/png";
        }

        ContentDisposition contentDisposition = ContentDisposition.attachment()
                .filename(fileName, java.nio.charset.StandardCharsets.UTF_8)
                .build();

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header("Content-Disposition", contentDisposition.toString())
                .body(bytes);
    }


    @Operation(summary="Обновить описание фото")
    @PutMapping("/{photoId}/description")
    public EventPhotoDTO updatePhotoDescription(@PathVariable UUID photoId, @RequestBody String desc) {
        return service.updatePhotoDescription(photoId, desc);
    }

    @Operation(summary="Удалить фото по photoId")
    @DeleteMapping("/{photoId}")
    public void deletePhoto(@PathVariable UUID photoId) {
        service.deletePhoto(photoId);
    }
}


