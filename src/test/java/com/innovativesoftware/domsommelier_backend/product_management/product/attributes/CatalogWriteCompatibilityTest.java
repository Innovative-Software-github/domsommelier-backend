package com.innovativesoftware.domsommelier_backend.product_management.product.attributes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.attributes.CatalogAttributes.*;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.write.*;
import com.innovativesoftware.domsommelier_backend.product_management.product.entity.*;
import com.innovativesoftware.domsommelier_backend.product_management.product.entity.wine.*;
import com.innovativesoftware.domsommelier_backend.product_management.product.repository.*;
import com.innovativesoftware.domsommelier_backend.product_management.product.service.write.WineWriteStrategy;
import com.innovativesoftware.domsommelier_backend.product_management.product.attributes.AttributeReferenceService;
import org.junit.jupiter.api.Test;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CatalogWriteCompatibilityTest {
    @Test void oldUpdatePreservesExtensionsAndExplicitNullClearsThem() throws Exception {
        var countries = mock(ProductCountryRepository.class, withSettings().mockMaker(org.mockito.MockMakers.SUBCLASS));
        var categories = mock(ProductCategoryRepository.class, withSettings().mockMaker(org.mockito.MockMakers.SUBCLASS));
        var wines = mock(WineRepository.class, withSettings().mockMaker(org.mockito.MockMakers.SUBCLASS));
        var colors = mock(WineColorRepository.class, withSettings().mockMaker(org.mockito.MockMakers.SUBCLASS));
        var types = mock(WineTypeRepository.class, withSettings().mockMaker(org.mockito.MockMakers.SUBCLASS));
        when(countries.findById(anyString())).thenReturn(Optional.of(new com.innovativesoftware.domsommelier_backend.customer_management.customer.entity.ProductCountry()));
        when(categories.findByName(any())).thenReturn(Optional.of(new ProductCategory()));
        when(colors.findById(anyString())).thenReturn(Optional.of(new WineColor()));
        ObjectMapper json = new ObjectMapper();
        var existing = new Wine(); var product = new Product(); existing.setProduct(product);
        var attributes = json.readValue("{\"strength\":12.5}", WineAttributes.class);
        existing.setExtendedDetails(attributes); product.setBrand(new Reference("bruni", "Bruni"));
        UUID id = UUID.randomUUID(); when(wines.findById(id)).thenReturn(Optional.of(existing)); when(wines.save(any())).thenAnswer(i -> i.getArgument(0));
        var strategy = new WineWriteStrategy(countries, categories, wines, colors, types);
        String base = "\"category\":\"wine\",\"article\":\"T-1\",\"name\":\"Test\",\"country\":\"Италия\",\"price\":1000,\"initialPrice\":1000,\"productionYear\":2025,\"volume\":0.75,\"color\":\"Белое\"";
        strategy.update(id, json.readValue("{" + base + "}", ProductWriteRequest.class));
        assertEquals(attributes, existing.getExtendedDetails()); assertEquals("bruni", product.getBrand().code());
        strategy.update(id, json.readValue("{" + base + ",\"extendedDetails\":null,\"brand\":null}", ProductWriteRequest.class));
        assertNull(existing.getExtendedDetails()); assertNull(product.getBrand());
    }
    @Test void whiskyFieldsCannotBeWrittenToCognac() throws Exception {
        var attributes = new ObjectMapper().readValue("{\"whiskyDetails\":{\"ageStatementStatus\":\"unknown\"}}", SpiritAttributes.class);
        assertThrows(org.springframework.web.server.ResponseStatusException.class, () -> AttributeReferenceService.validateSpiritSubtype("Коньяк", attributes));
        assertDoesNotThrow(() -> AttributeReferenceService.validateSpiritSubtype("Виски", attributes));
    }
}
