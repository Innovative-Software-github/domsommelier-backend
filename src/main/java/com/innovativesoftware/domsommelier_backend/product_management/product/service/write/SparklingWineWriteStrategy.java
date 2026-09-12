package com.innovativesoftware.domsommelier_backend.product_management.product.service.write;

import com.innovativesoftware.domsommelier_backend.product_management.product.entity.Product;
import com.innovativesoftware.domsommelier_backend.product_management.product.entity.champaigne_sparkling.SparklingWine;
import com.innovativesoftware.domsommelier_backend.product_management.product.entity.champaigne_sparkling.SparklingWineCategory;
import com.innovativesoftware.domsommelier_backend.product_management.product.entity.champaigne_sparkling.SparklingWineColor;
import com.innovativesoftware.domsommelier_backend.product_management.product.entity.champaigne_sparkling.SugarContent;
import com.innovativesoftware.domsommelier_backend.product_management.product.enums.ProductCategoryEnum;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.write.ProductWriteRequest;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.write.SparklingWriteRequest;
import com.innovativesoftware.domsommelier_backend.product_management.product.repository.ProductCategoryRepository;
import com.innovativesoftware.domsommelier_backend.product_management.product.repository.ProductCountryRepository;
import com.innovativesoftware.domsommelier_backend.product_management.product.repository.SparklingWineCategoryRepository;
import com.innovativesoftware.domsommelier_backend.product_management.product.repository.SparklingWineColorRepository;
import com.innovativesoftware.domsommelier_backend.product_management.product.repository.SparklingWineRepository;
import com.innovativesoftware.domsommelier_backend.product_management.product.repository.SugarContentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class SparklingWineWriteStrategy extends AbstractProductWriteStrategy {

    private final SparklingWineRepository sparklingWineRepository;
    private final SparklingWineCategoryRepository sparklingWineCategoryRepository;
    private final SparklingWineColorRepository sparklingWineColorRepository;
    private final SugarContentRepository sugarContentRepository;

    public SparklingWineWriteStrategy(ProductCountryRepository productCountryRepository,
                                      ProductCategoryRepository productCategoryRepository,
                                      SparklingWineRepository sparklingWineRepository,
                                      SparklingWineCategoryRepository sparklingWineCategoryRepository,
                                      SparklingWineColorRepository sparklingWineColorRepository,
                                      SugarContentRepository sugarContentRepository) {
        super(productCountryRepository, productCategoryRepository);
        this.sparklingWineRepository = sparklingWineRepository;
        this.sparklingWineCategoryRepository = sparklingWineCategoryRepository;
        this.sparklingWineColorRepository = sparklingWineColorRepository;
        this.sugarContentRepository = sugarContentRepository;
    }

    @Override
    public boolean supports(ProductCategoryEnum category) {
        return category == ProductCategoryEnum.champagne_and_sparkling;
    }

    @Override
    public Product create(ProductWriteRequest request) {
        SparklingWriteRequest req = cast(request, SparklingWriteRequest.class);
        SparklingWine sparkling = new SparklingWine();
        sparkling.setProduct(newBaseProduct(req));
        applyFields(sparkling, req);
        return sparklingWineRepository.save(sparkling).getProduct();
    }

    @Override
    public Product update(UUID id, ProductWriteRequest request) {
        SparklingWriteRequest req = cast(request, SparklingWriteRequest.class);
        SparklingWine sparkling = sparklingWineRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Игристое не найдено: " + id));
        applyBaseFields(sparkling.getProduct(), req);
        applyFields(sparkling, req);
        return sparklingWineRepository.save(sparkling).getProduct();
    }

    @Override
    public void delete(UUID id) {
        sparklingWineRepository.deleteById(id);
    }

    private void applyFields(SparklingWine sparkling, SparklingWriteRequest req) {
        if (req.isExtendedDetailsProvided()) sparkling.setExtendedDetails(req.getExtendedDetails());
        sparkling.setSubcategory(resolveSubcategory(req.getSubcategory()));
        sparkling.setSugarContent(resolveSugarContent(req.getSugarContent()));
        sparkling.setColor(resolveColor(req.getColor()));
        sparkling.setProducer(trimToNull(req.getProducer()));
        sparkling.setVolume(req.getVolume());
        sparkling.setFeatures(replaceStrings(sparkling.getFeatures(), req.getFeatures()));
    }

    private SparklingWineCategory resolveSubcategory(String name) {
        return sparklingWineCategoryRepository.findById(name.trim())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Подкатегория игристого не найдена: " + name));
    }

    private SugarContent resolveSugarContent(String name) {
        return sugarContentRepository.findById(name.trim())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Содержание сахара не найдено: " + name));
    }

    private SparklingWineColor resolveColor(String name) {
        return sparklingWineColorRepository.findById(name.trim())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Цвет игристого не найден: " + name));
    }
}
