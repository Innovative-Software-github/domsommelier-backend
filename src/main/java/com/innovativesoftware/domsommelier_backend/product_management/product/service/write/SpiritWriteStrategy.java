package com.innovativesoftware.domsommelier_backend.product_management.product.service.write;

import com.innovativesoftware.domsommelier_backend.product_management.product.entity.Product;
import com.innovativesoftware.domsommelier_backend.product_management.product.entity.spirit.Spirit;
import com.innovativesoftware.domsommelier_backend.product_management.product.entity.spirit.SpiritCategory;
import com.innovativesoftware.domsommelier_backend.product_management.product.enums.ProductCategoryEnum;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.write.ProductWriteRequest;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.write.SpiritWriteRequest;
import com.innovativesoftware.domsommelier_backend.product_management.product.repository.ProductCategoryRepository;
import com.innovativesoftware.domsommelier_backend.product_management.product.repository.ProductCountryRepository;
import com.innovativesoftware.domsommelier_backend.product_management.product.repository.SpiritCategoryRepository;
import com.innovativesoftware.domsommelier_backend.product_management.product.repository.SpiritRepository;
import com.innovativesoftware.domsommelier_backend.product_management.product.util.RussianLabelTranslator;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class SpiritWriteStrategy extends AbstractProductWriteStrategy {

    private final SpiritRepository spiritRepository;
    private final SpiritCategoryRepository spiritCategoryRepository;

    public SpiritWriteStrategy(ProductCountryRepository productCountryRepository,
                               ProductCategoryRepository productCategoryRepository,
                               SpiritRepository spiritRepository,
                               SpiritCategoryRepository spiritCategoryRepository) {
        super(productCountryRepository, productCategoryRepository);
        this.spiritRepository = spiritRepository;
        this.spiritCategoryRepository = spiritCategoryRepository;
    }

    @Override
    public boolean supports(ProductCategoryEnum category) {
        return category == ProductCategoryEnum.spirit;
    }

    @Override
    public Product create(ProductWriteRequest request) {
        SpiritWriteRequest req = cast(request, SpiritWriteRequest.class);
        Spirit spirit = new Spirit();
        spirit.setProduct(newBaseProduct(req));
        applyFields(spirit, req);
        return spiritRepository.save(spirit).getProduct();
    }

    @Override
    public Product update(UUID id, ProductWriteRequest request) {
        SpiritWriteRequest req = cast(request, SpiritWriteRequest.class);
        Spirit spirit = spiritRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Крепкий алкоголь не найден: " + id));
        applyBaseFields(spirit.getProduct(), req);
        applyFields(spirit, req);
        return spiritRepository.save(spirit).getProduct();
    }

    @Override
    public void delete(UUID id) {
        spiritRepository.deleteById(id);
    }

    private void applyFields(Spirit spirit, SpiritWriteRequest req) {
        if (req.isExtendedDetailsProvided()) spirit.setExtendedDetails(req.getExtendedDetails());
        if (spirit.getExtendedDetails() != null) {
            com.innovativesoftware.domsommelier_backend.product_management.product.attributes.AttributeReferenceService
                .validateSpiritSubtype(req.getSubcategory(), spirit.getExtendedDetails());
        }
        spirit.setCategory(resolveSubcategory(req.getSubcategory()));
        spirit.setStrength(req.getStrength());
        spirit.setProducer(trimToNull(req.getProducer()));
        spirit.setVolume(req.getVolume());
        // См. WineWriteStrategy — форма админки подтягивает уже переведённые
        // значения, untranslate нормализует их назад к исходному коду.
        spirit.setFeatures(replaceStrings(spirit.getFeatures(), RussianLabelTranslator.untranslateFeatures(req.getFeatures())));
    }

    private SpiritCategory resolveSubcategory(String name) {
        return spiritCategoryRepository.findById(name.trim())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Подкатегория крепкого не найдена: " + name));
    }
}
