package com.github.gepi.libr.repository;

import com.github.gepi.libr.entity.Author;
import com.github.gepi.libr.entity.BookAuthors;
import com.github.gepi.libr.entity.BookAuthorsId;
import io.jmix.core.repository.JmixDataRepository;

import java.util.List;

public interface BookAuthorsRepository extends JmixDataRepository<BookAuthors, BookAuthorsId> {
    public List<BookAuthors> findByAuthor(Author author);
}
