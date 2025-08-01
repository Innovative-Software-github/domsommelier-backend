package com.innovativesoftware.domsommelier_backend.infrastructure;

import lombok.Getter;

public final class BucketRegistry {

    private BucketRegistry() {}

    @Getter
    public enum Bucket {
        EVENT("event"),
        USERS("users"),
        NEWS("news"),
        PRODUCT("product");

        private final String name;

        Bucket(String name) {
            this.name = name;
        }

    }
}
