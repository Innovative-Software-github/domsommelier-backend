package com.innovativesoftware.domsommelier_backend.event_management.event.util;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * Построение URL фото мероприятия для фронтендов — по аналогии с
 * {@link com.innovativesoftware.domsommelier_backend.product_management.product.util.ProductPhotoUrls}.
 * <p>
 * До этого EventPhoto.url строился через {@code FileOperationService.fileUrl()},
 * который отдаёт "сырой" адрес MinIO (внутренний docker-хост вида
 * {@code http://minio:9000/...}) — недоступный из браузера. Плюс сами
 * download-эндпоинты бэкенда ({@code /events/files/name}, {@code /events/files/id})
 * искали объект по одному имени файла, без подпапки eventId, под которой файл
 * реально лежит в MinIO (см. MinioService.uploadOneFile) — то есть падали 500.
 * Здесь и то, и то собрано в одном месте с полным ключом объекта.
 */
public final class EventPhotoUrls {

    private static final String PUBLIC_PREFIX = "/events/files/name?file=";

    private EventPhotoUrls() {
    }

    public static String publicUrl(UUID eventId, String fileName) {
        if (eventId == null || fileName == null) {
            return null;
        }
        String objectKey = eventId + "/" + fileName;
        return PUBLIC_PREFIX + URLEncoder.encode(objectKey, StandardCharsets.UTF_8);
    }
}
