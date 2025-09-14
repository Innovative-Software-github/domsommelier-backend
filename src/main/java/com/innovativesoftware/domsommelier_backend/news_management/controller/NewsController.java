package com.innovativesoftware.domsommelier_backend.news_management.controller;

import com.innovativesoftware.domsommelier_backend.news_management.model.NewsWithFileListDTO;
import com.innovativesoftware.domsommelier_backend.news_management.model.NewsWithFilesRequest;
import com.innovativesoftware.domsommelier_backend.news_management.model.NewsWithoutFilesRequest;
import com.innovativesoftware.domsommelier_backend.news_management.service.NewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/news")
public class NewsController {

    @Autowired
    private NewService newsService;

    @GetMapping("")
    public List<NewsWithFileListDTO> findAllEvents() { return newsService.findAllNews();}

    @PostMapping("")
    public ResponseEntity<String> addNews(@RequestBody NewsWithoutFilesRequest newsWithoutFilesRequest) {
        return ResponseEntity.ok(String.valueOf(newsService.addNews(newsWithoutFilesRequest)));
    }

    @GetMapping("/id")
    public ResponseEntity<NewsWithFileListDTO> findNewsById(@RequestParam String id) {
        return ResponseEntity.ok(newsService.findNewsById(UUID.fromString(id)));
    }

    @DeleteMapping("/id")
    public void deleteNewsById(@RequestParam String id) {
        newsService.deleteNewsById(id);
    }

    @PutMapping("")
    public void updateNews(@RequestBody NewsWithFilesRequest newsWithFilesRequest) {
        try {
            newsService.updateNews(newsWithFilesRequest);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Not found");
        }
    }
}
