package com.innovativesoftware.domsommelier_backend.news_management.controller;

import com.innovativesoftware.domsommelier_backend.infrastructure.security.RequiresAdmin;
import com.innovativesoftware.domsommelier_backend.news_management.model.NewsDto;
import com.innovativesoftware.domsommelier_backend.news_management.model.NewsRequest;
import com.innovativesoftware.domsommelier_backend.news_management.service.NewsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.time.Duration;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@CrossOrigin
@RequestMapping("/api/v1/news")
@RequiredArgsConstructor
@Tag(name = "News", description = "Новости витрины")
public class NewsController {

    private final NewsService newsService;

    @GetMapping
    @Operation(summary = "Список новостей (свежие сверху, с пагинацией)")
    public Page<NewsDto> getNews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return newsService.getNews(PageRequest.of(page, size));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Новость по id")
    public NewsDto getById(@PathVariable UUID id) {
        return newsService.getById(id);
    }

    @GetMapping("/{id}/cover")
    @Operation(summary = "Обложка новости (изображение)")
    public ResponseEntity<byte[]> getCover(@PathVariable UUID id) {
        return newsService.getCover(id)
                .map(cover -> ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(detectImageContentType(cover.fileName())))
                        .cacheControl(CacheControl.maxAge(Duration.ofDays(7)).cachePublic())
                        .body(cover.bytes()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private static String detectImageContentType(String fileName) {
        String name = fileName.toLowerCase();
        if (name.endsWith(".png")) return "image/png";
        if (name.endsWith(".jpg") || name.endsWith(".jpeg")) return "image/jpeg";
        if (name.endsWith(".webp")) return "image/webp";
        if (name.endsWith(".gif")) return "image/gif";
        if (name.endsWith(".svg")) return "image/svg+xml";
        return MediaType.APPLICATION_OCTET_STREAM_VALUE;
    }

    @PostMapping
    @RequiresAdmin
    @Operation(summary = "Создать новость")
    public ResponseEntity<NewsDto> create(@RequestBody @Valid NewsRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(newsService.create(request));
    }

    @PutMapping("/{id}")
    @RequiresAdmin
    @Operation(summary = "Обновить новость")
    public NewsDto update(@PathVariable UUID id, @RequestBody @Valid NewsRequest request) {
        return newsService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @RequiresAdmin
    @Operation(summary = "Удалить новость")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        newsService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(path = "/{id}/cover", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @RequiresAdmin
    @Operation(summary = "Загрузить или заменить обложку новости")
    public NewsDto uploadCover(@PathVariable UUID id, @RequestPart("file") MultipartFile file) {
        return newsService.setCover(id, file);
    }

    @DeleteMapping("/{id}/cover")
    @RequiresAdmin
    @Operation(summary = "Удалить обложку новости")
    public NewsDto removeCover(@PathVariable UUID id) {
        return newsService.removeCover(id);
    }
}
