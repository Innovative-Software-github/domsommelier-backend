package com.innovativesoftware.domsommelier_backend.configuration;

import io.minio.MinioClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfig {
    private static final Logger log = LoggerFactory.getLogger(MinioConfig.class);
    @Value("${minio.url}")
    private String url;

    @Value("${minio.access.name}")
    private String accessKey;

    @Value("${minio.access.secret}")
    private String accessSecret;

    @Bean
    public MinioClient minioClient() {
        log.info("PLEEEEASE PRINT" + url + " " + accessKey + " " + accessSecret);
        return MinioClient.builder()
                .endpoint(url)
                .credentials(accessKey, accessSecret)
                .build();
    }
}
