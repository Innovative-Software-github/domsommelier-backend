package com.innovativesoftware.domsommelier_backend.customer_management.customer.controller;

import com.innovativesoftware.domsommelier_backend.auth_management.AppUserDetails;
import com.innovativesoftware.domsommelier_backend.customer_management.customer.entity.Customer;
import com.innovativesoftware.domsommelier_backend.customer_management.customer.model.CustomerProfileDto;
import com.innovativesoftware.domsommelier_backend.customer_management.customer.model.CustomerUpdateDto;
import com.innovativesoftware.domsommelier_backend.customer_management.customer_recommendations.model.CustomerRecommendationsDTO;
import com.innovativesoftware.domsommelier_backend.customer_management.customer_recommendations.repository.CustomerRepository;
import com.innovativesoftware.domsommelier_backend.customer_management.customer_recommendations.service.CustomerRecommendationsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
//@Hidden
@RequiredArgsConstructor
@RequestMapping("/customer")
public class CustomerController {
    private final CustomerRecommendationsService customerRecommendationsService;
    private final CustomerRepository customerRepository;

    @GetMapping("{id}/recommendations")
    public List<CustomerRecommendationsDTO> getUserRecommendations(@PathVariable("id") String userId) {
        return customerRecommendationsService.getUserRecommendations(userId);
    }

    @Operation(summary = "Получить профиль", description = "Возвращает данные текущего авторизованного пользователя")
    @GetMapping("/profile")
    public ResponseEntity<CustomerProfileDto> getProfile(
            @Parameter(hidden = true) @AuthenticationPrincipal AppUserDetails userDetails
    ) {
        Customer customer = getCustomer(userDetails.getId());

        return ResponseEntity.ok(CustomerProfileDto.builder()
                .id(customer.getId())
                .firstName(customer.getFirstName())
                .secondName(customer.getSecondName())
                .middleName(customer.getMiddleName())
                .email(customer.getEmail())
                .phone(customer.getPhone())
                .build());
    }

    @Operation(summary = "Обновить профиль", description = "Обновляет имя, фамилию, отчество, телефон")
    @PutMapping("/profile")
    public ResponseEntity<CustomerProfileDto> updateProfile(
            @Parameter(hidden = true) @AuthenticationPrincipal AppUserDetails userDetails,
            @RequestBody CustomerUpdateDto dto
    ) {
        Customer customer = getCustomer(userDetails.getId());

        if (dto.getFirstName() != null) customer.setFirstName(dto.getFirstName());
        if (dto.getSecondName() != null) customer.setSecondName(dto.getSecondName());
        if (dto.getMiddleName() != null) customer.setMiddleName(dto.getMiddleName());
        if (dto.getPhone() != null) customer.setPhone(dto.getPhone());

        customerRepository.save(customer);

        return ResponseEntity.ok(CustomerProfileDto.builder()
                .id(customer.getId())
                .firstName(customer.getFirstName())
                .secondName(customer.getSecondName())
                .middleName(customer.getMiddleName())
                .email(customer.getEmail())
                .phone(customer.getPhone())
                .build());
    }

    private Customer getCustomer(UUID id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Профиль не найден"));
    }
}

