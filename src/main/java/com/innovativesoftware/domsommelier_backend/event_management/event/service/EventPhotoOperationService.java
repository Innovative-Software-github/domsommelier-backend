package com.innovativesoftware.domsommelier_backend.event_management.event.service;

import com.innovativesoftware.domsommelier_backend.event_management.event.entity.Event;
import com.innovativesoftware.domsommelier_backend.event_management.event.entity.EventPhoto;
import com.innovativesoftware.domsommelier_backend.event_management.event.repository.EventPhotoRepository;
import com.innovativesoftware.domsommelier_backend.event_management.event.repository.EventRepository;
import com.innovativesoftware.domsommelier_backend.file_management.service.FileOperationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class EventPhotoOperationService extends FileOperationService {
    @Autowired
    private EventPhotoRepository eventPhotoRepository;

    @Autowired
    private EventRepository eventRepository;

    @Transactional
    public void uploadFilesWithRef(MultipartFile[] files, String bucket, String eventId) {
        List<MultipartFile> uploadedFiles = fileService.uploadFiles(files, bucket);
        Event event = eventRepository.getReferenceById(UUID.fromString(eventId));

        List<EventPhoto> readyFiles = uploadedFiles.stream()
                .map(uploadedFile -> {
                    try {
                        EventPhoto eventPhoto = new EventPhoto();
                        eventPhoto.setName(uploadedFile.getOriginalFilename());
                        eventPhoto.setBucket(bucket);
                        eventPhoto.setEvent(event);
                        return eventPhoto;
                    }
                    catch(Exception e) {
                        throw new RuntimeException("Problem with uploading event photos");
                    }
                })
                .toList();
        eventPhotoRepository.saveAll(readyFiles);
    }
}
