package com.innovativesoftware.domsommelier_backend.user.controller;

import com.innovativesoftware.domsommelier_backend.user.service.UserRecommendationsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRecommendationsService userRecommendationsService;

    @GetMapping("{id}/recommendations")
    public String getUserRecommendations(@PathVariable("id") String userId) {
        return userRecommendationsService.getUserRecommendations(userId);
    }
}

