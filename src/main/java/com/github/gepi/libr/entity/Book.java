package com.github.gepi.libr.entity;

import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.metamodel.annotation.InstanceName;
import io.jmix.core.metamodel.annotation.JmixEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@JmixEntity
@Table(name = "BOOK")
@Entity
public class Book {
    @JmixGeneratedValue
    @Column(name = "ID", nullable = false)
    @Id
    private Long id;

    @InstanceName
    @Column(name = "TITLE_RU", nullable = false, length = 1000)
    @NotNull
    private String titleRu;

    @Column(name = "TITLE_EN", nullable = false, length = 1000)
    @NotNull
    private String titleEn;

    @Column(name = "FILE_NAME_RU")
    private String fileNameRu;

    @Column(name = "FILE_NAME_EN")
    private String fileNameEn;

    @JoinColumn(name = "OWNER_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private User owner;

    @Column(name = "VERSION", nullable = false)
    @Version
    private Integer version;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "book", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<BookAuthors> bookAuthors = new ArrayList<>();

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public String getFileNameRu() {
        return fileNameRu;
    }

    public void setFileNameRu(String fileNameRu) {
        this.fileNameRu = fileNameRu;
    }

    public String getFileNameEn() {
        return fileNameEn;
    }

    public void setFileNameEn(String fileNameEn) {
        this.fileNameEn = fileNameEn;
    }

    public String getTitleRu() {
        return titleRu;
    }

    public void setTitleRu(String title) {
        this.titleRu = title;
    }

    public String getTitleEn() {
        return titleEn;
    }

    public void setTitleEn(String title) {
        this.titleEn = title;
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

    public List<BookAuthors> getBookAuthors() {
        return bookAuthors;
    }

    public void setBookAuthors(List<BookAuthors> bookAuthors) {
        this.bookAuthors = bookAuthors;
    }

    public void addAuthor(Author author) {
        BookAuthors bookAuthorsLine = new BookAuthors();
        bookAuthorsLine.setAuthor(author);
        bookAuthorsLine.setBook(this);
        bookAuthorsLine.setDefaut(false);

        bookAuthors.add(bookAuthorsLine);
    }

    public void removeAuthor(Author author) {
        bookAuthors.remove(0);
    }
}