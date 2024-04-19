package com.github.gepi.libr.entity;

import io.jmix.core.entity.annotation.JmixId;
import io.jmix.core.metamodel.annotation.JmixEntity;

@JmixEntity
public class TagTreeDTO {
    @JmixId
    private Long id;

    private TagScheme scheme;
    private Tag tag;

    private TagTreeDTO parent;

    public TagTreeDTO() {
    }

    public TagTreeDTO(Tag tag, TagTreeDTO parent, TagScheme scheme) {
        this(tag, scheme);
        this.parent = parent;
    }

    public TagTreeDTO(Tag tag, TagScheme scheme) {
        this.tag = tag;
        this.id = tag.getId();
    }
    public TagTreeDTO getParent() {
        return parent;
    }

    public void setParent(TagTreeDTO parent) {
        this.parent = parent;
    }

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.id = tag.getId();
        this.tag = tag;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TagScheme getScheme() {
        return scheme;
    }
}