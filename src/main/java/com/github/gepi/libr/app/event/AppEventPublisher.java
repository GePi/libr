package com.github.gepi.libr.app.event;

public interface AppEventPublisher {
    void subscribe(AppEventListener listener);

    void unSubscribe(AppEventListener listener);
}
