package com.innovativesoftware.domsommelier_backend.admin_management.customer.mapper;

import com.innovativesoftware.domsommelier_backend.customer_management.customer.entity.Customer;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class CustomerDisplayMapper {

    public String resolveDisplayName(Customer customer) {
        String fullName = Stream.of(
                        customer.getSecondName(),
                        customer.getFirstName(),
                        customer.getMiddleName()
                )
                .filter(part -> part != null && !part.isBlank())
                .collect(Collectors.joining(" "));

        return fullName.isEmpty() ? customer.getEmail() : fullName;
    }
}
