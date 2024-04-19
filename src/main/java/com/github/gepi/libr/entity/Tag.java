package com.github.gepi.libr.entity;

import io.jmix.core.DeletePolicy;
import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.entity.annotation.OnDeleteInverse;
import io.jmix.core.metamodel.annotation.InstanceName;
import io.jmix.core.metamodel.annotation.JmixEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@JmixEntity
@Table(name = "TAG", indexes = {
        @Index(name = "IDX_SCHEME", columnList = "SCHEME_ID")
})
@Entity
public class Tag {
    @Column(name = "ID", nullable = false)
    @Id
    @JmixGeneratedValue
    private Long id;

    @OnDeleteInverse(DeletePolicy.CASCADE)
    @JoinColumn(name = "SCHEME_ID", nullable = false)
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private TagScheme scheme;

    @InstanceName
    @Column(name = "TITLE", nullable = false)
    @NotNull
    private String title;

    @Column(name = "VERSION", nullable = false)
    @Version
    private Integer version;

    public Tag() {
    }

    public Tag(String title, User owner) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
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