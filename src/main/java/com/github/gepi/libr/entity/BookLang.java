package com.github.gepi.libr.entity;

import io.jmix.core.metamodel.datatype.impl.EnumClass;

import javax.annotation.Nullable;


public enum BookLang implements EnumClass<String> {

    RU("RU"),
    EN("EN");

    private final String id;

    BookLang(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static BookLang fromId(String id) {
        for (BookLang at : BookLang.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}