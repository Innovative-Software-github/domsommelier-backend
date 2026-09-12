package com.innovativesoftware.domsommelier_backend.product_management.product.importing;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.write.ProductWriteRequest;
import jakarta.validation.Validation;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/** Validates generated Python payloads against the actual Java DTO, without database writes. */
@EnabledIfSystemProperty(named="simplewine.plan", matches=".+")
class SimpleWineImportContractTest {
    @TestFactory List<DynamicTest> generatedProductsMatchWriteContract() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        var root = mapper.readTree(Path.of(System.getProperty("simplewine.plan")).toFile());
        List<DynamicTest> tests = new ArrayList<>();
        for (var item : root.get("products")) {
            String source = item.get("source").get("externalId").asText();
            tests.add(DynamicTest.dynamicTest(source, () -> {
                ObjectNode payload = ((ObjectNode) item.get("payload")).deepCopy();
                // Test-only prices/ID, never written back to the import plan or catalog.
                payload.put("article", "CONTRACT-TEST-" + source);
                payload.put("initialPrice", 1000); payload.put("price", 1500);
                var request = mapper.treeToValue(payload, ProductWriteRequest.class);
                try (var factory = Validation.buildDefaultValidatorFactory()) {
                    var violations = factory.getValidator().validate(request);
                    assertTrue(violations.isEmpty(), violations.toString());
                }
            }));
        }
        assertFalse(tests.isEmpty(), "Import plan must contain products");
        return tests;
    }
}
