package com.innovativesoftware.domsommelier_backend.product_management.product.model;
import com.innovativesoftware.domsommelier_backend.file_management.model.EntityWithFiles;
import com.innovativesoftware.domsommelier_backend.file_management.model.FileDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.UUID;

@Accessors(chain = true)
@Getter
@Setter
public class WineWithPhotosDTO implements WineProjection, EntityWithFiles<FileDTO> {
    private UUID id;
    private String name;
    private Integer price;
    private Integer discount;
    private String description;
    private String countryName;
    private String regionName;
    private String color;
    private Integer sugar;
    private Float volume;
    private List<FileDTO> files;
}
