package com.github.gepi.libr.app;

import com.github.gepi.libr.entity.Book;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class BookTagsEditorHelperFactory {

    private final ApplicationContext applicationContext;

    public BookTagsEditorHelperFactory(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public BookTagsEditorHelper create(Book book) {
        BookTagsEditorHelper bean = applicationContext.getBean(BookTagsEditorHelper.class);
        bean.setBook(book);
        return bean;
    }


}
