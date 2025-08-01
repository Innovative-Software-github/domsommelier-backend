package com.innovativesoftware.domsommelier_backend.infrastructure;

import io.minio.MinioClient;
import io.minio.SetBucketPolicyArgs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

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

    @Autowired
    private Environment env;

    @Bean
    public MinioClient minioClient() {
        MinioClient client = MinioClient.builder()
                .endpoint(url)
                .credentials(accessKey, accessSecret)
                .build();

        try {

            for (BucketRegistry.Bucket bucketEnum : BucketRegistry.Bucket.values()) {
                String bucket = bucketEnum.getName();

                // 1. Создать бакет, если его нет
                boolean exists = client.bucketExists(
                        io.minio.BucketExistsArgs.builder().bucket(bucket).build()
                );
                if (!exists) {
                    client.makeBucket(io.minio.MakeBucketArgs.builder().bucket(bucket).build());
                    log.info("Minio bucket '{}' создан автоматически.", bucket);
                } else {
                    log.info("Minio bucket '{}' уже существует.", bucket);
                }

                String policyJson = getPublicReadPolicy(bucket);
                client.setBucketPolicy(
                        SetBucketPolicyArgs.builder()
                                .bucket(bucket)
                                .config(policyJson)
                                .build()
                );
                log.info("READ-ONLY policy applied to bucket '{}'", bucket);

                // 2. Загрузить стартовые файлы для этого бакета (если указаны в пропертях)
                String filesProperty = "minio.init." + bucket + ".files";
                String[] files = env.getProperty(filesProperty, "").split(",");
                for (String filename : files) {
                    filename = filename.trim();
                    if (filename.isEmpty()) continue;

                    boolean filePresent = false;
                    try {
                        client.statObject(io.minio.StatObjectArgs.builder()
                                .bucket(bucket)
                                .object(filename)
                                .build());
                        filePresent = true;
                    } catch (io.minio.errors.ErrorResponseException e) {
                        if (!"NoSuchKey".equals(e.errorResponse().code())) {
                            throw e;
                        }
                    } catch (Exception ignored) {}

                    if (filePresent) {
                        log.info("Файл '{}' уже есть в бакете '{}'", filename, bucket);
                        continue;
                    }

                    try (InputStream in = getClass().getClassLoader().getResourceAsStream("init_photos/" + filename)) {
                        if (in == null) {
                            log.warn("Файл {} не найден в ресурсах!", filename);
                            continue;
                        }
                        client.putObject(io.minio.PutObjectArgs.builder()
                                .bucket(bucket)
                                .object(filename)
                                .stream(in, -1, 10 * 1024 * 1024)
                                .contentType("image/jpeg")
                                .build());
                        log.info("Файл '{}' добавлен в бакет '{}'", filename, bucket);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Ошибка при инициализации Minio", e);
            throw new RuntimeException(e);
        }

        return client;
    }

    private String getPublicReadPolicy(String bucket) {
        return "{\n" +
                "  \"Version\": \"2012-10-17\",\n" +
                "  \"Statement\": [\n" +
                "    {\n" +
                "      \"Effect\": \"Allow\",\n" +
                "      \"Principal\": {\"AWS\": [\"*\"]},\n" +
                "      \"Action\": [\"s3:GetObject\"],\n" +
                "      \"Resource\": [\"arn:aws:s3:::" + bucket + "/*\"]\n" +
                "    }\n" +
                "  ]\n" +
                "}";
    }
}
