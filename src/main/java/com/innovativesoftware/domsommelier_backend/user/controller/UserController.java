package com.innovativesoftware.domsommelier_backend.user.controller;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @GetMapping("{id}/recommendations")
    public void getUserRecommendations(@PathVariable("id") String userId) {

    }
}

