package com.innovativesoftware.domsommelier_backend.shared.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class FileId implements Serializable {
    private String bucket;
    private String name;
}
