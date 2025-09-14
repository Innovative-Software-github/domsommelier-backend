package com.innovativesoftware.domsommelier_backend.customer_management.customer.controller;

import com.innovativesoftware.domsommelier_backend.customer_management.customer_recommendations.model.CustomerRecommendationsDTO;
import com.innovativesoftware.domsommelier_backend.customer_management.customer_recommendations.service.CustomerRecommendationsService;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Hidden
@RequestMapping("/customer")
public class CustomerController {
    @Autowired
    private CustomerRecommendationsService customerRecommendationsService;

    @GetMapping("{id}/recommendations")
    public List<CustomerRecommendationsDTO> getUserRecommendations(@PathVariable("id") String userId) {
        return customerRecommendationsService.getUserRecommendations(userId);
    }
}

