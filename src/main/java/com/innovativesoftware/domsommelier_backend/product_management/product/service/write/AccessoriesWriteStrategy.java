package com.innovativesoftware.domsommelier_backend.product_management.product.service.write;

import com.innovativesoftware.domsommelier_backend.product_management.product.entity.Product;
import com.innovativesoftware.domsommelier_backend.product_management.product.entity.accessories.Accessories;
import com.innovativesoftware.domsommelier_backend.product_management.product.enums.ProductCategoryEnum;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.write.AccessoriesWriteRequest;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.write.ProductWriteRequest;
import com.innovativesoftware.domsommelier_backend.product_management.product.repository.AccessoriesRepository;
import com.innovativesoftware.domsommelier_backend.product_management.product.repository.ProductCategoryRepository;
import com.innovativesoftware.domsommelier_backend.product_management.product.repository.ProductCountryRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class AccessoriesWriteStrategy extends AbstractProductWriteStrategy {

    private final AccessoriesRepository accessoriesRepository;

    public AccessoriesWriteStrategy(ProductCountryRepository productCountryRepository,
                                    ProductCategoryRepository productCategoryRepository,
                                    AccessoriesRepository accessoriesRepository) {
        super(productCountryRepository, productCategoryRepository);
        this.accessoriesRepository = accessoriesRepository;
    }

    @Override
    public boolean supports(ProductCategoryEnum category) {
        return category == ProductCategoryEnum.accessories;
    }

    @Override
    public Product create(ProductWriteRequest request) {
        AccessoriesWriteRequest req = cast(request, AccessoriesWriteRequest.class);
        Accessories accessories = new Accessories();
        accessories.setProduct(newBaseProduct(req));
        applyFields(accessories, req);
        return accessoriesRepository.save(accessories).getProduct();
    }

    @Override
    public Product update(UUID id, ProductWriteRequest request) {
        AccessoriesWriteRequest req = cast(request, AccessoriesWriteRequest.class);
        Accessories accessories = accessoriesRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Аксессуар не найден: " + id));
        applyBaseFields(accessories.getProduct(), req);
        applyFields(accessories, req);
        return accessoriesRepository.save(accessories).getProduct();
    }

    @Override
    public void delete(UUID id) {
        accessoriesRepository.deleteById(id);
    }

    private void applyFields(Accessories accessories, AccessoriesWriteRequest req) {
        accessories.setProducer(trimToNull(req.getProducer()));
        accessories.setFeatures(replaceStrings(accessories.getFeatures(), req.getFeatures()));
    }
}
