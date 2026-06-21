package com.innovativesoftware.domsommelier_backend.product_management.product.model.write;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AccessoriesWriteRequest extends ProductWriteRequest {

    private String producer;

    private List<String> features;
}
