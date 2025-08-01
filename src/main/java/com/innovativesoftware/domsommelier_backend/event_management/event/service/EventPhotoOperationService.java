package com.innovativesoftware.domsommelier_backend.event_management.event.service;

import com.innovativesoftware.domsommelier_backend.event_management.event.entity.Event;
import com.innovativesoftware.domsommelier_backend.event_management.event.entity.EventPhoto;
import com.innovativesoftware.domsommelier_backend.event_management.event.model.EventPhotoDTO;
import com.innovativesoftware.domsommelier_backend.event_management.event.repository.EventPhotoRepository;
import com.innovativesoftware.domsommelier_backend.event_management.event.repository.EventRepository;
import com.innovativesoftware.domsommelier_backend.exceptions.InvalidValueException;
import com.innovativesoftware.domsommelier_backend.file_management.service.FileOperationService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class EventPhotoOperationService extends FileOperationService {

    private final EventPhotoRepository eventPhotoRepository;
    private final EventRepository eventRepository;

    /**
     * Загрузка фото к мероприятию.
     */
    @Transactional
    public List<EventPhotoDTO> uploadFilesWithRef(MultipartFile[] files, String bucket, String eventId, String description) {
        Event event = eventRepository.findById(UUID.fromString(eventId))
                .orElseThrow(() -> new NoSuchElementException("Event not found"));

        // Проверка, что файлы с такими именами уже есть (если есть, значит нафиг)
        for (MultipartFile mf : files) {
            String fileName = mf.getOriginalFilename();
            if (eventPhotoRepository.existsByUrlAndEventId(fileUrl(bucket, eventId, fileName), event.getId())) {
                throw new InvalidValueException(
                        "Файл с названием " + fileName + " уже существует", "INVALID_FILENAME", "Файл существует"
                );
            }
        }

        List<MultipartFile> uploadedFiles = fileService.uploadFiles(files, eventId, bucket);

        List<EventPhotoDTO> dtos = new ArrayList<>();
        for (MultipartFile mf : uploadedFiles) {
            EventPhoto photo = EventPhoto.builder()
                    .name(mf.getOriginalFilename()) // файл сохраняется под этим названием
                    .bucket(bucket)
                    .description(description)
                    .event(event)
                    .url(fileUrl(bucket, eventId, mf.getOriginalFilename()))
                    .build();
            eventPhotoRepository.save(photo);

            dtos.add(EventPhotoDTO.builder()
                    .id(photo.getId())
                    .eventId(event.getId())
                    .name(photo.getName())
                    .description(photo.getDescription())
                    .bucket(photo.getBucket())
                    .url(fileUrl(bucket, eventId, mf.getOriginalFilename()))
                    .build());
        }
        return dtos;
    }

    /**
     * Получить все фото для мероприятия.
     */
    public List<EventPhotoDTO> getPhotosByEvent(UUID eventId) {
        List<EventPhoto> photos = eventPhotoRepository.findAll()
                .stream()
                .filter(p -> p.getEvent().getId().equals(eventId))
                .toList();

        return photos.stream()
                .map(photo -> EventPhotoDTO.builder()
                        .id(photo.getId())
                        .eventId(eventId)
                        .name(photo.getName())
                        .bucket(photo.getBucket())
                        .description(photo.getDescription())
                        .url(fileUrl(photo.getBucket(), String.valueOf(eventId), photo.getName()))
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * Получить метаданные фото по photoId.
     */
    public EventPhotoDTO getPhotoInfo(UUID photoId) {
        EventPhoto photo = eventPhotoRepository.findById(photoId)
                .orElseThrow(() -> new NoSuchElementException("Photo not found"));
        return EventPhotoDTO.builder()
                .id(photo.getId())
                .eventId(photo.getEvent().getId())
                .name(photo.getName())
                .bucket(photo.getBucket())
                .description(photo.getDescription())
                .url(fileUrl(photo.getBucket(), String.valueOf(photo.getEvent().getId()), photo.getName()))
                .build();
    }

    /**
     * Скачать файл — использует getBytesFromFile из базового сервиса.
     */
    public byte[] getPhotoBytes(String bucket, String fileName) {
        return getBytesFromFile(bucket, fileName);
    }

    /**
     * Скачать фото по id (ищет файл в БД, получает имя файла и bucket и далее отдаёт байты)
     */
    public PhotoDownloadData getPhotoDataById(UUID photoId) {
        EventPhoto photo = eventPhotoRepository.findById(photoId)
                .orElseThrow(() -> new NoSuchElementException("Photo not found"));
        byte[] bytes = getBytesFromFile(photo.getBucket(), photo.getName());
        return new PhotoDownloadData(photo.getName(), bytes, fileUrl(photo.getBucket(),
                String.valueOf(photo.getEvent().getId()), photo.getName()));
    }

    /**
     * Вспомогательный класс для возвращения имени файла вместе с байтами
     */
    @Getter
    @AllArgsConstructor
    public static class PhotoDownloadData {
        private String fileName;
        private byte[] bytes;
        private String url;
    }

    /**
     * Обновить описание фото.
     */
    @Transactional
    public EventPhotoDTO updatePhotoDescription(UUID photoId, String newDescription) {
        EventPhoto photo = eventPhotoRepository.findById(photoId)
                .orElseThrow(() -> new NoSuchElementException("Photo not found"));
        photo.setDescription(newDescription);
        eventPhotoRepository.save(photo);
        return getPhotoInfo(photoId);
    }

    /**
     * Удалить файл (из Minio и из базы).
     */
    @Transactional
    public void deletePhoto(UUID photoId) {
        EventPhoto photo = eventPhotoRepository.findById(photoId)
                .orElseThrow(() -> new NoSuchElementException("Photo not found"));
        fileService.deleteFile(photo.getBucket(), photo.getName()); // реализуй этот метод в MinioService
        eventPhotoRepository.delete(photo);
    }
}
