package com.innovativesoftware.domsommelier_backend.customer_management.customer.controller;

import com.innovativesoftware.domsommelier_backend.customer_management.customer_recommendations.model.CustomerRecommendationsDTO;
import com.innovativesoftware.domsommelier_backend.customer_management.customer_recommendations.service.CustomerRecommendationsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customer")
public class CustomerController {
    @Autowired
    private CustomerRecommendationsService customerRecommendationsService;

    @GetMapping("{id}/recommendations")
    public List<CustomerRecommendationsDTO> getUserRecommendations(@PathVariable("id") String userId) {
        return customerRecommendationsService.getUserRecommendations(userId);
    }
}

