package com.github.gepi.libr.screen.book;

import com.github.gepi.libr.entity.Book;
import io.jmix.ui.UiComponents;
import io.jmix.ui.component.Component;
import io.jmix.ui.component.GroupTable;
import io.jmix.ui.component.Label;
import io.jmix.ui.screen.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.stream.Collectors;

@UiController("Book.browse")
@UiDescriptor("book-browse.xml")
@LookupComponent("booksTable")
public class BookBrowse extends StandardLookup<Book> {
    @Autowired
    private GroupTable<Book> booksTable;
    @Autowired
    private UiComponents uiComponents;

    @Subscribe
    public void onInit(final InitEvent event) {
        booksTable.addGeneratedColumn("authors", this::generateAuthorsColumn);
    }

    private Component generateAuthorsColumn(Book book) {
        String authorsNames = book.getBookAuthors().stream()
                .map(bookAuthor -> bookAuthor.getAuthor().toString()) // Предполагается, что у BookAuthors есть ссылка на Author
                .collect(Collectors.joining(", "));

        // Создаем лейбл с именами авторов
        Label<String> label = uiComponents.create(Label.TYPE_STRING);
        label.setValue(authorsNames);

        return label;
    }

}