package com.innovativesoftware.domsommelier_backend.product_management.product.service.write;

import com.innovativesoftware.domsommelier_backend.product_management.product.entity.Product;
import com.innovativesoftware.domsommelier_backend.product_management.product.entity.snack.Snack;
import com.innovativesoftware.domsommelier_backend.product_management.product.entity.snack.SnackCategory;
import com.innovativesoftware.domsommelier_backend.product_management.product.enums.ProductCategoryEnum;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.write.ProductWriteRequest;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.write.SnackWriteRequest;
import com.innovativesoftware.domsommelier_backend.product_management.product.repository.ProductCategoryRepository;
import com.innovativesoftware.domsommelier_backend.product_management.product.repository.ProductCountryRepository;
import com.innovativesoftware.domsommelier_backend.product_management.product.repository.SnackCategoryRepository;
import com.innovativesoftware.domsommelier_backend.product_management.product.repository.SnackRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class SnackWriteStrategy extends AbstractProductWriteStrategy {

    private final SnackRepository snackRepository;
    private final SnackCategoryRepository snackCategoryRepository;

    public SnackWriteStrategy(ProductCountryRepository productCountryRepository,
                              ProductCategoryRepository productCategoryRepository,
                              SnackRepository snackRepository,
                              SnackCategoryRepository snackCategoryRepository) {
        super(productCountryRepository, productCategoryRepository);
        this.snackRepository = snackRepository;
        this.snackCategoryRepository = snackCategoryRepository;
    }

    @Override
    public boolean supports(ProductCategoryEnum category) {
        return category == ProductCategoryEnum.snack;
    }

    @Override
    public Product create(ProductWriteRequest request) {
        SnackWriteRequest req = cast(request, SnackWriteRequest.class);
        Snack snack = new Snack();
        snack.setProduct(newBaseProduct(req));
        applyFields(snack, req);
        return snackRepository.save(snack).getProduct();
    }

    @Override
    public Product update(UUID id, ProductWriteRequest request) {
        SnackWriteRequest req = cast(request, SnackWriteRequest.class);
        Snack snack = snackRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Снек не найден: " + id));
        applyBaseFields(snack.getProduct(), req);
        applyFields(snack, req);
        return snackRepository.save(snack).getProduct();
    }

    @Override
    public void delete(UUID id) {
        snackRepository.deleteById(id);
    }

    private void applyFields(Snack snack, SnackWriteRequest req) {
        snack.setSubcategory(resolveSubcategory(req.getSubcategory()));
        snack.setProducer(trimToNull(req.getProducer()));
        snack.setPairings(replaceStrings(snack.getPairings(), req.getPairings()));
    }

    private SnackCategory resolveSubcategory(String name) {
        return snackCategoryRepository.findById(name.trim())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Подкатегория снека не найдена: " + name));
    }
}
