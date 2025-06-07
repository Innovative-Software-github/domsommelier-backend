package com.innovativesoftware.domsommelier_backend.news_management.controller;

import com.innovativesoftware.domsommelier_backend.news_management.service.NewsPhotoOperationService;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Hidden
@RequestMapping("/news/files")
public class NewsPhotoController {
    private final String BUCKET = "news";

    @Autowired
    private NewsPhotoOperationService fileOperationService;

    @PostMapping("upload")
    public void uploadPhoto(@RequestBody MultipartFile[] files, @RequestParam String newsId) {
        fileOperationService.uploadFilesWithRef(files, BUCKET, newsId);
    }

    @GetMapping("")
    public byte[] downloadPhoto(@RequestParam("file") String fileName) {
        return fileOperationService.getBytesFromFile(BUCKET, fileName);
    }
}
