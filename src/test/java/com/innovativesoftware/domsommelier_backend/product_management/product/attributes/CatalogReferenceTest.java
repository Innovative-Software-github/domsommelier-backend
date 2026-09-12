package com.innovativesoftware.domsommelier_backend.product_management.product.attributes;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.attributes.CatalogAttributes.Reference;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.write.ProductWriteRequest;
import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import java.util.Optional;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class CatalogReferenceTest {
    @Test void referencesMustExistWithTheirCanonicalLabels() throws Exception {
        var repository = mock(AttributeReferenceRepository.class, withSettings().mockMaker(org.mockito.MockMakers.SUBCLASS));
        var service = new AttributeReferenceService(repository, Validation.buildDefaultValidatorFactory().getValidator());
        var request = new ObjectMapper().readValue("""
            {"category":"wine","article":"T","name":"Test","country":"Италия","price":1000,"initialPrice":1000,
             "productionYear":2025,"volume":0.75,"color":"Белое","brand":{"code":"bruni","label":"Bruni"}}
            """, ProductWriteRequest.class);
        when(repository.findById("brand:bruni")).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> service.validate(request));
        var reference = new AttributeReference(); reference.setCode("bruni"); reference.setLabel("Bruni");
        when(repository.findById("brand:bruni")).thenReturn(Optional.of(reference));
        assertDoesNotThrow(() -> service.validate(request));
        request.setBrand(new Reference("bruni", "Другое название"));
        assertThrows(ResponseStatusException.class, () -> service.validate(request));
    }
    @Test void duplicateLabelsAndUnknownKindsAreRejected() {
        var repository = mock(AttributeReferenceRepository.class, withSettings().mockMaker(org.mockito.MockMakers.SUBCLASS));
        var service = new AttributeReferenceService(repository, Validation.buildDefaultValidatorFactory().getValidator());
        when(repository.existsByKindAndLabelIgnoreCase("brand", "Bruni")).thenReturn(true);
        assertThrows(ResponseStatusException.class, () -> service.create("brand", new Reference("another_code", "Bruni")));
        assertThrows(ResponseStatusException.class, () -> service.list("unrecognized"));
    }
}
