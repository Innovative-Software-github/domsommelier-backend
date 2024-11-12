package com.innovativesoftware.domsommelier_backend.customer_management.customer.service;

import com.innovativesoftware.domsommelier_backend.customer_management.customer.entity.Customer;
import com.innovativesoftware.domsommelier_backend.customer_management.customer.repository.CustomerRepository;
import com.innovativesoftware.domsommelier_backend.customer_management.customer.model.CustomerRecommendationsProjection;
import com.innovativesoftware.domsommelier_backend.customer_management.customer.model.CustomerRecommendationsDTO;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;

@RequiredArgsConstructor
@Service
public class CustomerRecommendationsService {

    private static final Logger log = LoggerFactory.getLogger(CustomerRecommendationsService.class);
    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private CustomerRepository customerRepository;

    private final ModelMapper mapper = new ModelMapper();

    private final int LIMIT = 3;

    @Scheduled(fixedRate = 300000)
    public void runCustomerRecommendations() {
        log.info("Worker works!");
        Cache cache = cacheManager.getCache("customerRecommendationsCache");

        List<Customer> customers = customerRepository.findAll();
        for (Customer customer : customers) {
           List<CustomerRecommendationsDTO> userRecommendations = customerRepository.findCustomerRecommendations(customer.getId(), LIMIT)
                   .stream()
                   .map((CustomerRecommendationsProjection customerRecommendationsProjection) ->
                           mapper.map(customerRecommendationsProjection, CustomerRecommendationsDTO.class))
                   .toList();
           cache.put(customer.getId().toString(), userRecommendations);
        }
    }

    public List<CustomerRecommendationsDTO> getUserRecommendations(String userId) {
        Cache cache = cacheManager.getCache("customerRecommendationsCache");
        if (cache == null) {
            return null;
        }
        return cache.get(userId, List.class);
    }
}
