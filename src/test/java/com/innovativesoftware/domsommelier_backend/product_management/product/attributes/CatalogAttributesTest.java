package com.innovativesoftware.domsommelier_backend.product_management.product.attributes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.attributes.CatalogAttributes.*;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.write.*;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.wine.WineDetailsDto;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;

class CatalogAttributesTest {
    private final ObjectMapper json = new ObjectMapper();
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    private static final String BASE = "\"category\":\"wine\",\"article\":\"T-1\",\"name\":\"Test\",\"country\":\"Франция\",\"price\":1000,\"initialPrice\":1000,\"productionYear\":2025,\"volume\":0.75,\"color\":\"Белое\"";

    @Test void oldRequestAndExplicitNullHaveDifferentSemantics() throws Exception {
        var absent = (WineWriteRequest) json.readValue("{" + BASE + "}", ProductWriteRequest.class);
        assertFalse(absent.isExtendedDetailsProvided()); assertFalse(absent.isBrandProvided());
        assertTrue(validator.validate(absent).isEmpty());
        var cleared = (WineWriteRequest) json.readValue("{" + BASE + ",\"extendedDetails\":null,\"brand\":null,\"packaging\":null}", ProductWriteRequest.class);
        assertTrue(cleared.isExtendedDetailsProvided()); assertNull(cleared.getExtendedDetails());
        assertTrue(cleared.isBrandProvided()); assertTrue(cleared.isPackagingProvided());
    }

    @Test void nestedAttributesRoundTripAndReadFlattening() throws Exception {
        String data = """
            {"region":{"code":"marlborough","label":"Мальборо"},"strength":12.5,
             "grapeComposition":[{"grape":{"code":"sauvignon_blanc","label":"Совиньон блан"},"percent":100}],
             "grapeCompositionComplete":true,
             "sensoryProfile":{"acidity":{"value":3,"scaleMin":null,"scaleMax":null,"sourceId":"SW-160429"}},
             "aging":{"status":"aged","durationMonths":{"min":36,"max":null,"minInclusive":false,"maxInclusive":true}},
             "servingTemperature":{"min":10,"max":12}}
            """;
        var request = (WineWriteRequest) json.readValue("{" + BASE + ",\"extendedDetails\":" + data + "}", ProductWriteRequest.class);
        assertTrue(validator.validate(request).isEmpty(), validator.validate(request).toString());
        var attributes = request.getExtendedDetails();
        assertEquals(attributes, json.readValue(json.writeValueAsString(attributes), WineAttributes.class));
        var read = WineDetailsDto.builder().productionYear(2025).extendedDetails(attributes).build();
        var tree = json.valueToTree(read);
        assertEquals("marlborough", tree.path("region").path("code").asText());
        assertFalse(tree.has("extendedDetails"));
        assertFalse(json.valueToTree(attributes).has("compositionValid"));
    }

    @Test void invalidCompositionsAndRatingsAreRejected() throws Exception {
        for (String extension : new String[] {
            "{\"strength\":101}",
            "{\"grapeCompositionComplete\":true}",
            "{\"grapeComposition\":[{\"grape\":{\"code\":\"merlot\",\"label\":\"Мерло\"},\"percent\":60},{\"grape\":{\"code\":\"merlot\",\"label\":\"Мерло\"},\"percent\":50}]}",
            "{\"servingTemperature\":{\"min\":20,\"max\":10}}",
            "{\"sensoryProfile\":{\"body\":{\"value\":6,\"scaleMin\":1,\"scaleMax\":5,\"sourceId\":\"source\"}}}",
            "{\"sensoryProfile\":{\"body\":{\"value\":3}}}",
            "{\"aging\":{\"status\":\"not_aged\",\"durationMonths\":{\"min\":12,\"max\":12,\"minInclusive\":true,\"maxInclusive\":true}}}"
        }) {
            var request = json.readValue("{" + BASE + ",\"extendedDetails\":" + extension + "}", ProductWriteRequest.class);
            assertFalse(validator.validate(request).isEmpty(), extension);
        }
    }

    @Test void unknownIsNotNonVintageAndNasIsNotZeroYears() throws Exception {
        var unknown = json.readValue("{\"vintageStatus\":\"unknown\"}", SparklingAttributes.class);
        assertTrue(validator.validate(unknown).isEmpty());
        var invalid = json.readValue("{\"productionYear\":2025,\"vintageStatus\":\"non_vintage\"}", SparklingAttributes.class);
        assertFalse(validator.validate(invalid).isEmpty());
        assertTrue(validator.validate(new WhiskySpecific(null, null, null, null, AgeStatementStatus.nas)).isEmpty());
        assertFalse(validator.validate(new WhiskySpecific(null, null, null, 3, AgeStatementStatus.nas)).isEmpty());
    }

    @Test void strictDurationBoundsArePreserved() {
        assertTrue(validator.validate(new DurationRange(new BigDecimal("36"), null, false, true)).isEmpty());
        assertFalse(validator.validate(new DurationRange(new BigDecimal("36"), new BigDecimal("36"), false, true)).isEmpty());
    }
}
