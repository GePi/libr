package com.github.gepi.libr.app.event;

import org.springframework.context.ApplicationEvent;

public interface AppEventListener {
    void publish(ApplicationEvent event);
}
