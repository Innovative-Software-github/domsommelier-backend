package com.innovativesoftware.domsommelier_backend.product_test.controller;

import com.innovativesoftware.domsommelier_backend.product_test.service.TestDataLoader;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/product-test")
public class TestController {

    @Autowired
    private BeanFactory beanFactory;

    @PostMapping("")
    public void initializeTestData(@RequestParam("datarows") int dataRows) {
        TestDataLoader testDataLoader = beanFactory.getBean(TestDataLoader.class);
        testDataLoader.initializeProductsWithRandomData(dataRows);
    }
}

