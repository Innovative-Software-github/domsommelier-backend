package com.innovativesoftware.domsommelier_backend.product_management.product.model.wine;
import com.innovativesoftware.domsommelier_backend.file_management.model.EntityWithFiles;
import com.innovativesoftware.domsommelier_backend.file_management.model.FileDTO;
import lombok.*;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Accessors(chain = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WineWithPhotosDTO implements WineProjection, EntityWithFiles<FileDTO> {
    private UUID id;
    private String name;
    private Integer price;
    private BigDecimal salePrice;
    private List<FileDTO> files;
}
