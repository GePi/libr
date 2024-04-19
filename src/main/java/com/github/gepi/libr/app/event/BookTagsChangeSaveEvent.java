package com.github.gepi.libr.app.event;

import org.springframework.context.ApplicationEvent;

public class BookTagsChangeSaveEvent extends ApplicationEvent{
    private final Object source;
    public BookTagsChangeSaveEvent(Object source) {
        super(source);
        this.source = source;
    }

    public Object getSource() {
        return source;
    }
}
