package com.github.gepi.libr.app.event;

import org.springframework.context.ApplicationEvent;
public class TreeTagNodeSelectedEvent extends ApplicationEvent {
    public TreeTagNodeSelectedEvent(Object source) {
        super(source);
    }
}