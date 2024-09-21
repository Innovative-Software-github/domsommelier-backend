package com.innovativesoftware.domsommelier_backend.user.service;

import com.innovativesoftware.domsommelier_backend.entity.Customer;
import com.innovativesoftware.domsommelier_backend.user.CustomerRepository;
import com.innovativesoftware.domsommelier_backend.user.model.ComplexDTO;
import com.innovativesoftware.domsommelier_backend.user.model.CustomerTopPurchasesDTO;
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
public class UserRecommendationsService {

    private static final Logger log = LoggerFactory.getLogger(UserRecommendationsService.class);
    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private CustomerRepository customerRepository;

    private ModelMapper mapper = new ModelMapper();

    private int LIMIT = 3;

    @Scheduled(fixedRate = 30000)
    public void runUserRecommendations() {
        log.info("Scheduler works every 5 secs");
        Cache cache = cacheManager.getCache("userRecommendationsCache");

        List<Customer> customers = customerRepository.findAll();
        for (Customer customer : customers) {
           List<CustomerTopPurchasesDTO> topPurchases = customerRepository.findPurchasesOfCustomer(customer.getId(), LIMIT)
                   .stream()
                   .map((ComplexDTO complexDTO) -> mapper.map(complexDTO, CustomerTopPurchasesDTO.class))
                   .toList();
           cache.put(customer.getId().toString(), topPurchases);
        }
    }

    public List<CustomerTopPurchasesDTO> getUserRecommendations(String userId) {
        Cache cache = cacheManager.getCache("userRecommendationsCache");
        if (cache == null) {
            return null;
        }
        return cache.get(userId, List.class);
    }
}
