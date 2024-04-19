package com.github.gepi.libr.entity;

import io.jmix.core.metamodel.annotation.JmixEntity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@JmixEntity
@Embeddable
public class BookTagsId implements Serializable {
    @Column(name = "BOOK_ID")
    private Long bookId;

    @Column(name = "TAG_ID")
    private Long tagId;

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public Long getTagId() {
        return tagId;
    }

    public void setTagId(Long tagId) {
        this.tagId = tagId;
    }
}