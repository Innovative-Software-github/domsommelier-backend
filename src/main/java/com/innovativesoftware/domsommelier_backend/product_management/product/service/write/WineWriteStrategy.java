package com.innovativesoftware.domsommelier_backend.product_management.product.service.write;

import com.innovativesoftware.domsommelier_backend.product_management.product.entity.Product;
import com.innovativesoftware.domsommelier_backend.product_management.product.entity.wine.Wine;
import com.innovativesoftware.domsommelier_backend.product_management.product.entity.wine.WineColor;
import com.innovativesoftware.domsommelier_backend.product_management.product.entity.wine.WineType;
import com.innovativesoftware.domsommelier_backend.product_management.product.enums.ProductCategoryEnum;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.write.ProductWriteRequest;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.write.WineWriteRequest;
import com.innovativesoftware.domsommelier_backend.product_management.product.repository.ProductCategoryRepository;
import com.innovativesoftware.domsommelier_backend.product_management.product.repository.ProductCountryRepository;
import com.innovativesoftware.domsommelier_backend.product_management.product.repository.WineColorRepository;
import com.innovativesoftware.domsommelier_backend.product_management.product.repository.WineRepository;
import com.innovativesoftware.domsommelier_backend.product_management.product.repository.WineTypeRepository;
import com.innovativesoftware.domsommelier_backend.product_management.product.util.RussianLabelTranslator;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class WineWriteStrategy extends AbstractProductWriteStrategy {

    private final WineRepository wineRepository;
    private final WineColorRepository wineColorRepository;
    private final WineTypeRepository wineTypeRepository;

    public WineWriteStrategy(ProductCountryRepository productCountryRepository,
                             ProductCategoryRepository productCategoryRepository,
                             WineRepository wineRepository,
                             WineColorRepository wineColorRepository,
                             WineTypeRepository wineTypeRepository) {
        super(productCountryRepository, productCategoryRepository);
        this.wineRepository = wineRepository;
        this.wineColorRepository = wineColorRepository;
        this.wineTypeRepository = wineTypeRepository;
    }

    @Override
    public boolean supports(ProductCategoryEnum category) {
        return category == ProductCategoryEnum.wine;
    }

    @Override
    public Product create(ProductWriteRequest request) {
        WineWriteRequest wineRequest = cast(request, WineWriteRequest.class);
        Product product = newBaseProduct(wineRequest);

        Wine wine = new Wine();
        wine.setProduct(product); // @MapsId: id вина копируется из product при persist
        applyWineFields(wine, wineRequest);

        return wineRepository.save(wine).getProduct(); // cascade=ALL сохраняет и базовый Product
    }

    @Override
    public Product update(UUID id, ProductWriteRequest request) {
        WineWriteRequest wineRequest = cast(request, WineWriteRequest.class);
        Wine wine = wineRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Вино не найдено: " + id));

        applyBaseFields(wine.getProduct(), wineRequest);
        applyWineFields(wine, wineRequest);

        return wineRepository.save(wine).getProduct();
    }

    @Override
    public void delete(UUID id) {
        wineRepository.deleteById(id); // cascade=ALL удаляет и базовый Product
    }

    private void applyWineFields(Wine wine, WineWriteRequest request) {
        if (request.isExtendedDetailsProvided()) wine.setExtendedDetails(request.getExtendedDetails());
        wine.setProductionYear(request.getProductionYear());
        wine.setColor(resolveColor(request.getColor()));
        wine.setType(resolveType(request.getType()));
        wine.setProducer(trimToNull(request.getProducer()));
        wine.setVolume(request.getVolume());
        // Форма редактирования в админке подтягивает текущие значения товара
        // через тот же эндпоинт, что и витрина — то есть уже переведённые на
        // русский (см. WineDetailsMapper). Если админ сохранит форму, не
        // трогая эти поля, придёт русская подпись вместо исходного кода —
        // untranslate возвращает известные подписи назад, а всё остальное
        // (новый сорт/особенность, которых нет в словаре) пропускает как есть.
        wine.setGrapes(replaceStrings(wine.getGrapes(), RussianLabelTranslator.untranslateGrapes(request.getGrapes())));
        if (wine.getExtendedDetails() != null
                && wine.getExtendedDetails().grapeComposition() != null) {
            wine.setGrapes(replaceStrings(wine.getGrapes(), wine.getExtendedDetails().grapeComposition()
                .stream().map(share -> share.grape().code()).toList()));
        }
        wine.setFeatures(replaceStrings(wine.getFeatures(), RussianLabelTranslator.untranslateFeatures(request.getFeatures())));
    }

    private WineColor resolveColor(String name) {
        return wineColorRepository.findById(name.trim())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Цвет вина не найден: " + name));
    }

    private WineType resolveType(String name) {
        if (name == null || name.isBlank()) {
            return null;
        }
        return wineTypeRepository.findById(name.trim())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Тип вина не найден: " + name));
    }
}
