package com.innovativesoftware.domsommelier_backend.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Service
public class UserRecommendationsService {
    @Cacheable(cacheNames = "userRecommendationsCache", key = "#userId")
    public String getUserRecommendations(String userId) {
        try {
            TimeUnit.SECONDS.sleep(3);
            return "Amogus";
        } catch(Exception e) {
            return null;
        }
    }
}
