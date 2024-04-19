package com.github.gepi.libr.app.event;

import com.github.gepi.libr.app.event.AppEventPublisher;
import com.github.gepi.libr.app.event.BookTagsChangeSaveEvent;
import com.github.gepi.libr.app.event.AppEventListener;
import com.github.gepi.libr.entity.BookTags;
import io.jmix.core.event.EntityChangedEvent;
import io.jmix.core.event.EntitySavingEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.HashSet;
import java.util.Set;

@Component
public class BookTagsEntityEventListener implements AppEventPublisher {

    private final ApplicationEventPublisher eventPublisher;
    private final Set<AppEventListener> listeners = new HashSet<>();

    public BookTagsEntityEventListener(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @EventListener
    public void onBookTagsChangedBeforeCommit(final EntityChangedEvent<BookTags> event) {
    }

    @TransactionalEventListener
    public void onBookTagsChangedAfterCommit(final EntityChangedEvent<BookTags> event) {
        for (var listener : listeners) {
            listener.publish(new BookTagsChangeSaveEvent(event));
        }

    }

    @EventListener
    public void onBookTagsSaving(final EntitySavingEvent<BookTags> event) {
    }

    @Override
    public void subscribe(AppEventListener listener) {
        listeners.add(listener);
    }

    @Override
    public void unSubscribe(AppEventListener listener) {
        listeners.remove(listener);
    }
}