package com.github.gepi.libr.entity;

import io.jmix.core.DeletePolicy;
import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.entity.annotation.OnDeleteInverse;
import io.jmix.core.metamodel.annotation.JmixEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@JmixEntity
@Table(name = "TAG_HIERARCHY", indexes = {
        @Index(name = "IDX_TAG_HIERARCHY_TG_PR", columnList = "TAG_ID, PARENT_ID", unique = true)
})
@Entity
public class TagHierarchy {
    @JmixGeneratedValue
    @Column(name = "ID", nullable = false)
    @Id
    private Long id;

    @Column(name = "VERSION", nullable = false)
    @Version
    private Integer version;

    @OnDeleteInverse(DeletePolicy.CASCADE)
    @JoinColumn(name = "SCHEME_ID", nullable = false)
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private TagScheme scheme;

    @JoinColumn(name = "TAG_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Tag tag;

    @JoinColumn(name = "PARENT_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Tag parent;

    public TagHierarchy() {
    }

    public TagHierarchy(TagScheme scheme, Tag tag, Tag parent) {
        this.tag = tag;
        this.parent = parent;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Tag getParent() {
        return parent;
    }

    public void setParent(Tag parent) {
        this.parent = parent;
    }

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
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

    public void setScheme(TagScheme scheme) {
        this.scheme = scheme;
    }
}