package com.innovativesoftware.domsommelier_backend.file_management.model;

import java.util.List;

public interface EntityWithFiles<T> {
    List<T> getFiles();
}
