package com.github.gepi.libr.app.event;

import org.springframework.context.ApplicationEvent;

public class BookTagsEditorTagListChangedEvent extends ApplicationEvent {
    public BookTagsEditorTagListChangedEvent(Object source) {
        super(source);
    }
}
