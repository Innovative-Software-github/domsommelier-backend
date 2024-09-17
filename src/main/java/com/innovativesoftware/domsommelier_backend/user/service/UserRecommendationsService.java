package com.innovativesoftware.domsommelier_backend.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserRecommendationsService {
    public String getUserRecommendations(String userId) {
        return userId;
    }
}
