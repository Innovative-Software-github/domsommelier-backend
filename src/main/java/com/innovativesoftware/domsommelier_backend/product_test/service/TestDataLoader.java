package com.innovativesoftware.domsommelier_backend.product_test.service;

import com.innovativesoftware.domsommelier_backend.product_test.repository.ProductTestRepository;
import com.innovativesoftware.domsommelier_backend.product_test.repository.WineTestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

public class TestDataLoader implements ApplicationRunner {

    @Autowired
    private ProductTestRepository productTestRepository;

    @Autowired
    private WineTestRepository wineTestRepository;

    private void initializeProductsWithRandomData(int dataRows) {

    }

    public void run(ApplicationArguments args) {
    }
}
