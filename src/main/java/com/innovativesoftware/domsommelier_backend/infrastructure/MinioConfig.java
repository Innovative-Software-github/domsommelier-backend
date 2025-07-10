package com.innovativesoftware.domsommelier_backend.infrastructure;

import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.InputStream;

@Slf4j
@Configuration
public class MinioConfig {

    @Value("${minio.url}")
    private String url;

    @Value("${minio.access.name}")
    private String accessKey;

    @Value("${minio.access.secret}")
    private String accessSecret;

    private static final String BUCKET_NAME = "event";

    private static final String[] FILENAMES = {
            "italian-wine-casino-small.jpg",
            "italian-wine-casino-large.jpg",
            "french-wine-tasting-small.jpg",
            "french-wine-tasting-large.jpg",
            "spanish-wine-tasting-small.jpg",
            "spanish-wine-tasting-large.jpg"
    };

    @Bean
    public MinioClient minioClient() {
        MinioClient client = MinioClient.builder()
                .endpoint(url)
                .credentials(accessKey, accessSecret)
                .build();

        try {
            boolean exists = client.bucketExists(
                    io.minio.BucketExistsArgs.builder().bucket(BUCKET_NAME).build()
            );
            if (!exists) {
                client.makeBucket(
                        io.minio.MakeBucketArgs.builder().bucket(BUCKET_NAME).build()
                );
                log.info("Minio bucket '{}' создан автоматически.", BUCKET_NAME);
            } else {
                log.info("Minio bucket '{}' уже существует.", BUCKET_NAME);
            }

            // Загрузка файлов из ресурсов
            ClassLoader classLoader = getClass().getClassLoader();
            for (String filename : FILENAMES) {
                // Проверка: если файл уже есть в бакете, не грузим
                boolean filePresent = false;
                try {
                    client.statObject(io.minio.StatObjectArgs.builder()
                            .bucket(BUCKET_NAME)
                            .object(filename)
                            .build());
                    filePresent = true;
                } catch (io.minio.errors.ErrorResponseException e) {
                    if (!"NoSuchKey".equals(e.errorResponse().code())) {
                        throw e;
                    }
                } catch (Exception ignored) {
                }
                if (filePresent) {
                    log.info("Файл '{}' уже есть в бакете '{}'", filename, BUCKET_NAME);
                    continue;
                }

                try (InputStream in = classLoader.getResourceAsStream("init_photos/" + filename)) {
                    if (in == null) {
                        log.warn("Файл {} не найден в ресурсах!", filename);
                        continue;
                    }
                    client.putObject(io.minio.PutObjectArgs.builder()
                            .bucket(BUCKET_NAME)
                            .object(filename)
                            .stream(in, -1, 10 * 1024 * 1024)
                            .contentType("image/jpeg")
                            .build());
                    log.info("Файл '{}' добавлен в бакет '{}'", filename, BUCKET_NAME);
                }
            }
        } catch (Exception e) {
            log.error("Ошибка при инициализации Minio", e);
            throw new RuntimeException(e);
        }

        return client;
    }
}
