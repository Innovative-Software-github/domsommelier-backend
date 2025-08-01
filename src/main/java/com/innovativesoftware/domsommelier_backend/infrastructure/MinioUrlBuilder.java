package com.innovativesoftware.domsommelier_backend.infrastructure;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MinioUrlBuilder {
    @Value("${minio.url}")
    private String baseUrl;

    public String getPublicFileUrl(String bucket, String path, String fileName) {
        String normPath = (path == null || path.isBlank()) ? "" : path.replaceAll("^/+", "").replaceAll("/+$", "") + "/";
        return String.format("%s/%s/%s%s", baseUrl.replaceAll("/+$", ""), bucket, normPath, fileName);
    }
}
