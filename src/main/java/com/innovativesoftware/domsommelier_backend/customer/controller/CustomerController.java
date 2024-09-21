package com.innovativesoftware.domsommelier_backend.customer.controller;

import com.innovativesoftware.domsommelier_backend.customer.model.CustomerRecommendationsDTO;
import com.innovativesoftware.domsommelier_backend.customer.service.CustomerRecommendationsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class CustomerController {
    @Autowired
    private CustomerRecommendationsService customerRecommendationsService;

    @GetMapping("{id}/recommendations")
    public List<CustomerRecommendationsDTO> getUserRecommendations(@PathVariable("id") String userId) {
        return customerRecommendationsService.getUserRecommendations(userId);
    }
}

