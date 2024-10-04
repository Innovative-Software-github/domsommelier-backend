package com.innovativesoftware.domsommelier_backend.product_test.service;

import com.innovativesoftware.domsommelier_backend.product_test.enums.ProductTypes;
import com.innovativesoftware.domsommelier_backend.product_test.model.ProductTest;
import com.innovativesoftware.domsommelier_backend.product_test.model.WineTest;
import com.innovativesoftware.domsommelier_backend.product_test.repository.ProductTestRepository;
import com.innovativesoftware.domsommelier_backend.product_test.repository.WineTestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
@Lazy
public class TestDataLoader {

    @Autowired
    private ProductTestRepository productTestRepository;

    @Autowired
    private WineTestRepository wineTestRepository;

    public void initializeProductsWithRandomData(int dataRows) {
        for (int i = 0; i < dataRows; ++i) {
            ProductTest productTest = new ProductTest();
            productTest.setType(ProductTypes.WINE);
            productTest.setPrice(100+i+1);

            WineTest wineTest = new WineTest();
            wineTest.setProduct(productTest);
            wineTest.setColor("Red");

            productTestRepository.save(productTest);
            wineTestRepository.save(wineTest);
        }
    }
}
