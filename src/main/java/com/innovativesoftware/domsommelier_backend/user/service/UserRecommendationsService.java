package com.innovativesoftware.domsommelier_backend.user.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Service
public class UserRecommendationsService {

    private static final Logger log = LoggerFactory.getLogger(UserRecommendationsService.class);
    @Autowired
    private CacheManager cacheManager;

    @Scheduled(fixedRate = 5000)
    public void runUserRecommendations() {
        log.info("Scheduler works every 5 secs");
        Cache cache = cacheManager.getCache("userRecommendationsCache");
        cache.put("dkskkdskdjksjdk", "Amogus");
    }

    public Optional<String> getUserRecommendations(String userId) {
        Cache cache = cacheManager.getCache("userRecommendationsCache");
        if (cache == null) {
            return Optional.empty();
        }
        String cachedValue = cache.get(userId, String.class);
        if (cachedValue == null) {
            return Optional.empty();
        }
        return cachedValue.describeConstable();
    }
}
