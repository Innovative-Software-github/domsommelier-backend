package com.innovativesoftware.domsommelier_backend.product_management.product.service.write;

import com.innovativesoftware.domsommelier_backend.product_management.product.entity.Product;
import com.innovativesoftware.domsommelier_backend.product_management.product.entity.low_alcohol.LowAlcohol;
import com.innovativesoftware.domsommelier_backend.product_management.product.entity.low_alcohol.LowAlcoholCategory;
import com.innovativesoftware.domsommelier_backend.product_management.product.enums.ProductCategoryEnum;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.write.LowAlcoholWriteRequest;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.write.ProductWriteRequest;
import com.innovativesoftware.domsommelier_backend.product_management.product.repository.LowAlcoholCategoryRepository;
import com.innovativesoftware.domsommelier_backend.product_management.product.repository.LowAlcoholRepository;
import com.innovativesoftware.domsommelier_backend.product_management.product.repository.ProductCategoryRepository;
import com.innovativesoftware.domsommelier_backend.product_management.product.repository.ProductCountryRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class LowAlcoholWriteStrategy extends AbstractProductWriteStrategy {

    private final LowAlcoholRepository lowAlcoholRepository;
    private final LowAlcoholCategoryRepository lowAlcoholCategoryRepository;

    public LowAlcoholWriteStrategy(ProductCountryRepository productCountryRepository,
                                   ProductCategoryRepository productCategoryRepository,
                                   LowAlcoholRepository lowAlcoholRepository,
                                   LowAlcoholCategoryRepository lowAlcoholCategoryRepository) {
        super(productCountryRepository, productCategoryRepository);
        this.lowAlcoholRepository = lowAlcoholRepository;
        this.lowAlcoholCategoryRepository = lowAlcoholCategoryRepository;
    }

    @Override
    public boolean supports(ProductCategoryEnum category) {
        return category == ProductCategoryEnum.low_alcohol;
    }

    @Override
    public Product create(ProductWriteRequest request) {
        LowAlcoholWriteRequest req = cast(request, LowAlcoholWriteRequest.class);
        LowAlcohol lowAlcohol = new LowAlcohol();
        lowAlcohol.setProduct(newBaseProduct(req));
        applyFields(lowAlcohol, req);
        return lowAlcoholRepository.save(lowAlcohol).getProduct();
    }

    @Override
    public Product update(UUID id, ProductWriteRequest request) {
        LowAlcoholWriteRequest req = cast(request, LowAlcoholWriteRequest.class);
        LowAlcohol lowAlcohol = lowAlcoholRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Слабоалкогольное не найдено: " + id));
        applyBaseFields(lowAlcohol.getProduct(), req);
        applyFields(lowAlcohol, req);
        return lowAlcoholRepository.save(lowAlcohol).getProduct();
    }

    @Override
    public void delete(UUID id) {
        lowAlcoholRepository.deleteById(id);
    }

    private void applyFields(LowAlcohol lowAlcohol, LowAlcoholWriteRequest req) {
        lowAlcohol.setCategory(resolveSubcategory(req.getSubcategory()));
        lowAlcohol.setStrength(req.getStrength());
        lowAlcohol.setProducer(trimToNull(req.getProducer()));
        lowAlcohol.setVolume(req.getVolume());
        lowAlcohol.setFeatures(replaceStrings(lowAlcohol.getFeatures(), req.getFeatures()));
    }

    private LowAlcoholCategory resolveSubcategory(String name) {
        return lowAlcoholCategoryRepository.findById(name.trim())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Подкатегория слабоалкогольного не найдена: " + name));
    }
}
