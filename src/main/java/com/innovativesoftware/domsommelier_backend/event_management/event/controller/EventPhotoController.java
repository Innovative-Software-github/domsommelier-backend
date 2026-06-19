package com.innovativesoftware.domsommelier_backend.event_management.event.controller;

import com.innovativesoftware.domsommelier_backend.event_management.event.model.EventPhotoDTO;
import com.innovativesoftware.domsommelier_backend.event_management.event.service.EventPhotoOperationService;
import com.innovativesoftware.domsommelier_backend.infrastructure.BucketRegistry;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import com.innovativesoftware.domsommelier_backend.infrastructure.security.RequiresAdmin;
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
    private final static String BUCKET = BucketRegistry.Bucket.EVENT.getName();

    @Operation(summary="Загрузка фото к мероприятию")
    @PostMapping(path = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @RequiresAdmin
    public List<EventPhotoDTO> uploadPhoto(
            @RequestPart("files") MultipartFile[] files,
            @RequestPart("description") String description,
            @RequestParam String eventId
    ) {
        return service.uploadFilesWithRef(files, BUCKET, eventId, description);
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
    @GetMapping("/name")
    public ResponseEntity<byte[]> downloadPhoto(@RequestParam("file") String fileName) {
        byte[] bytes = service.getPhotoBytes("event", fileName);

        String contentType;
        try {
            contentType = java.nio.file.Files.probeContentType(java.nio.file.Paths.get(fileName));
            if (contentType == null) {
                contentType = detectMimeTypeByExtension(fileName);
            }
        } catch (Exception e) {
            contentType = detectMimeTypeByExtension(fileName);
        }


        ContentDisposition contentDisposition = ContentDisposition.attachment()
                .filename(fileName, java.nio.charset.StandardCharsets.UTF_8)
                .build();

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header("Content-Disposition", contentDisposition.toString())
                .body(bytes);
    }

    @Operation(summary="Скачать фото по id")
    @GetMapping("/id")
    public ResponseEntity<byte[]> downloadPhotoById(@RequestParam("id") UUID photoId) {
        EventPhotoOperationService.PhotoDownloadData data = service.getPhotoDataById(photoId);

        String contentType;
        String fileName = data.getFileName();
        try {
            contentType = java.nio.file.Files.probeContentType(java.nio.file.Paths.get(fileName));
            if (contentType == null) {
                contentType = detectMimeTypeByExtension(fileName);
            }
        } catch (Exception e) {
            contentType = detectMimeTypeByExtension(fileName);
        }

        ContentDisposition contentDisposition = ContentDisposition.attachment()
                .filename(fileName, java.nio.charset.StandardCharsets.UTF_8)
                .build();

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header("Content-Disposition", contentDisposition.toString())
                .body(data.getBytes());
    }

    // Вспомогательный метод, можно вынести в утилиту
    private String detectMimeTypeByExtension(String fileName) {
        String name = fileName.toLowerCase();
        if (name.endsWith(".jpg") || name.endsWith(".jpeg")) return "image/jpeg";
        if (name.endsWith(".png")) return "image/png";
        if (name.endsWith(".gif")) return "image/gif";
        if (name.endsWith(".webp")) return "image/webp";
        if (name.endsWith(".svg")) return "image/svg+xml";
        if (name.endsWith(".bmp")) return "image/bmp";
        if (name.endsWith(".pdf")) return "application/pdf";
        return "application/octet-stream";
    }


    @Operation(summary="Обновить описание фото")
    @PutMapping("/{photoId}/description")
    @RequiresAdmin
    public EventPhotoDTO updatePhotoDescription(@PathVariable UUID photoId, @RequestBody String desc) {
        return service.updatePhotoDescription(photoId, desc);
    }

    @Operation(summary="Удалить фото по photoId")
    @DeleteMapping("/{photoId}")
    @RequiresAdmin
    public void deletePhoto(@PathVariable UUID photoId) {
        service.deletePhoto(photoId);
    }
}


