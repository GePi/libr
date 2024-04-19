package com.github.gepi.libr.entity;

import io.jmix.core.metamodel.annotation.JmixEntity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@JmixEntity
@Embeddable
public class BookAuthorsId implements Serializable {
    @Column(name = "BOOK_ID")
    private Long bookId;

    @Column(name = "AUTHOR_ID")
    private Long authorId;

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BookAuthorsId that = (BookAuthorsId) o;

        if (getBookId() != null ? !getBookId().equals(that.getBookId()) : that.getBookId() != null) return false;
        return getAuthorId() != null ? getAuthorId().equals(that.getAuthorId()) : that.getAuthorId() == null;
    }

    @Override
    public int hashCode() {
        int result = getBookId() != null ? getBookId().hashCode() : 0;
        result = 31 * result + (getAuthorId() != null ? getAuthorId().hashCode() : 0);
        return result;
    }
}