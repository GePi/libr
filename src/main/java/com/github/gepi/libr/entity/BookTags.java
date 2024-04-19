package com.github.gepi.libr.entity;

import io.jmix.core.DeletePolicy;
import io.jmix.core.entity.annotation.OnDeleteInverse;
import io.jmix.core.metamodel.annotation.JmixEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@JmixEntity
@Table(name = "BOOK_TAGS", indexes = {
        @Index(name = "IDX_BOOK_TAGS_BOOK", columnList = "BOOK_ID"),
        @Index(name = "IDX_BOOK_TAGS_TAG", columnList = "TAG_ID")
})
@Entity
public class BookTags {

    @EmbeddedId
    private BookTagsId id;

    @OnDeleteInverse(DeletePolicy.CASCADE)
    @JoinColumn(name = "BOOK_ID", nullable = false, updatable = false)
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("bookId")
    private Book book;

    @OnDeleteInverse(DeletePolicy.CASCADE)
    @JoinColumn(name = "TAG_ID", nullable = false, updatable = false)
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("tagId")
    private Tag tag;

    @Column(name = "VERSION", nullable = false)
    @Version
    private Integer version;

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public BookTagsId getId() {
        return id;
    }

    public void setId(BookTagsId id) {
        this.id = id;
    }
}