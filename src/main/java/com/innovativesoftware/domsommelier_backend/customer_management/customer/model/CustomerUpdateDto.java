package com.innovativesoftware.domsommelier_backend.customer_management.customer.model;

import lombok.Data;

@Data
public class CustomerUpdateDto {
    private String firstName;
    private String secondName;
    private String middleName;
    private String phone;
}