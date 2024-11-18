package com.innovativesoftware.domsommelier_backend.event_management.event.controller;

import com.innovativesoftware.domsommelier_backend.event_management.event.service.EventPhotoOperationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/events/files")
public class EventPhotoController {
    private final String BUCKET = "event";

    @Autowired
    private EventPhotoOperationService fileOperationService;

    @PostMapping("upload")
    public void uploadPhoto(@RequestBody MultipartFile[] files, @RequestParam String eventId) {
        fileOperationService.uploadFilesWithRef(files, BUCKET, eventId);
    }

    @GetMapping("")
    public byte[] downloadPhoto(@RequestParam("file") String fileName) {
        return fileOperationService.getBytesFromFile(BUCKET, fileName);
    }
}

