package com.github.gepi.libr.entity;

import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.metamodel.annotation.JmixEntity;

import javax.persistence.*;

@JmixEntity
@Table(name = "AUTHOR")
@Entity
public class Author {
    @JmixGeneratedValue
    @Column(name = "ID", nullable = false)
    @Id
    private Long id;

    @Column(name = "VERSION", nullable = false)
    @Version
    private Integer version;

    @Column(name = "NAME_EN")
    private String nameEn;

    @Column(name = "NAME_RU")
    private String nameRu;

    public String getNameEn() {
        return nameEn;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }

    public String getNameRu() {
        return nameRu;
    }

    public void setNameRu(String nameRu) {
        this.nameRu = nameRu;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Author author = (Author) o;

        if (!getId().equals(author.getId())) return false;
        if (getNameEn() != null ? !getNameEn().equals(author.getNameEn()) : author.getNameEn() != null) return false;
        return getNameRu() != null ? getNameRu().equals(author.getNameRu()) : author.getNameRu() == null;
    }

    @Override
    public int hashCode() {
        int result = getId().hashCode();
        result = 31 * result + (getNameEn() != null ? getNameEn().hashCode() : 0);
        result = 31 * result + (getNameRu() != null ? getNameRu().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder();
        if (nameRu != null && !nameRu.isEmpty()) res.append(nameRu);
        if (nameEn != null && !nameEn.isEmpty()) {
            if (res.isEmpty()) {
                res.append(nameRu);
            } else {
                res.append("(").append(nameEn).append(")");
            }
        }
        return res.toString();
    }
}