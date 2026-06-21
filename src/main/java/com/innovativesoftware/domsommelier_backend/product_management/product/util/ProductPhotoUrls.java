package com.innovativesoftware.domsommelier_backend.product_management.product.util;

import com.innovativesoftware.domsommelier_backend.file_management.model.FileDTO;
import com.innovativesoftware.domsommelier_backend.product_management.product.entity.Product;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

/**
 * Построение URL фото товара для фронтендов.
 * <p>
 * Фото отдаётся публичным download-эндпоинтом бэкенда через единый прокси-префикс
 * {@code /api-back} (его используют и витрина Next, и админка Vite). Объект в MinIO лежит
 * под ключом {@code productId/имя}, поэтому он и передаётся параметром {@code file}.
 */
public final class ProductPhotoUrls {

    private static final String PUBLIC_PREFIX = "/api-back/products/files?file=";

    private ProductPhotoUrls() {
    }

    public static String publicUrl(UUID productId, String fileName) {
        if (productId == null || fileName == null) {
            return null;
        }
        String objectKey = productId + "/" + fileName;
        return PUBLIC_PREFIX + URLEncoder.encode(objectKey, StandardCharsets.UTF_8);
    }

    public static List<FileDTO> toFileDtos(Product product) {
        return product.getProductPhoto().stream()
                .map(photo -> FileDTO.builder()
                        .id(photo.getId())
                        .bucket(photo.getBucket())
                        .name(photo.getName())
                        .description(photo.getDescription())
                        .url(publicUrl(product.getId(), photo.getName()))
                        .build())
                .toList();
    }
}
