package com.github.gepi.libr.entity;

import io.jmix.core.metamodel.annotation.JmixEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@JmixEntity
@Table(name = "BOOK_AUTHORS", indexes = {
        @Index(name = "IDX_BOOK_AUTHORS_BOOK", columnList = "BOOK_ID"),
        @Index(name = "IDX_BOOK_AUTHORS_AUTHOR", columnList = "AUTHOR_ID")
})
@Entity
public class BookAuthors {
    @EmbeddedId
    private BookAuthorsId bookAuthorsId;
    @JoinColumn(name = "BOOK_ID", nullable = false, updatable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @NotNull
    @MapsId("bookId")
    private Book book;
    @JoinColumn(name = "AUTHOR_ID", nullable = false, updatable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @NotNull
    @MapsId("authorId")
    private Author author;
    @Column(name = "DEFAUT")
    private Boolean defaut;
    @Column(name = "VERSION", nullable = false)
    @Version
    private Integer version;

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public Boolean getDefaut() {
        return defaut;
    }

    public void setDefaut(Boolean defaut) {
        this.defaut = defaut;
    }

    public BookAuthorsId getBookAuthorsId() {
        return bookAuthorsId;
    }

    public void setBookAuthorsId(BookAuthorsId bookAuthorsId) {
        this.bookAuthorsId = bookAuthorsId;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BookAuthors that = (BookAuthors) o;

        if (!getBookAuthorsId().equals(that.getBookAuthorsId())) return false;
        if (!getBook().equals(that.getBook())) return false;
        if (!getAuthor().equals(that.getAuthor())) return false;
        if (getDefaut() != null ? !getDefaut().equals(that.getDefaut()) : that.getDefaut() != null) return false;
        return getVersion() != null ? getVersion().equals(that.getVersion()) : that.getVersion() == null;
    }

    @Override
    public int hashCode() {
        int result = getBookAuthorsId().hashCode();
        result = 31 * result + getBook().hashCode();
        result = 31 * result + getAuthor().hashCode();
        result = 31 * result + (getDefaut() != null ? getDefaut().hashCode() : 0);
        result = 31 * result + (getVersion() != null ? getVersion().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        if (author==null){
            return "";
        }
        StringBuilder result = new StringBuilder();
        if (author.getNameRu() != null && !author.getNameRu().isEmpty() ){
            result.append(author.getNameRu());
        }
        if (author.getNameEn() != null && !author.getNameEn().isEmpty() ){
            if (result.isEmpty()) {
                result.append(author.getNameEn());
            } else {
                result.append("(").append(author.getNameEn()).append(")");
            }
        }
        return result.toString();
    }
}