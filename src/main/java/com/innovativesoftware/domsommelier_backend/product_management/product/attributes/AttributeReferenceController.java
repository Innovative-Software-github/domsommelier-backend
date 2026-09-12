package com.innovativesoftware.domsommelier_backend.product_management.product.attributes;

import com.innovativesoftware.domsommelier_backend.product_management.product.model.attributes.CatalogAttributes.Reference;
import com.innovativesoftware.domsommelier_backend.infrastructure.security.RequiresAdmin;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import java.util.List;

@RestController @RequestMapping("/api/v1/admin/products/attribute-references")
@RequiredArgsConstructor @RequiresAdmin
public class AttributeReferenceController {
    private final AttributeReferenceService service;
    @GetMapping("/{kind}")
    public List<Reference> list(@PathVariable String kind) { return service.list(kind); }
    @PostMapping("/{kind}") @ResponseStatus(HttpStatus.CREATED)
    public Reference create(@PathVariable String kind, @RequestBody @Valid Reference value) { return service.create(kind, value); }
}
