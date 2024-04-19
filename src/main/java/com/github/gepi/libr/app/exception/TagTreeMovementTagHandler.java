package com.github.gepi.libr.app.exception;

import io.jmix.ui.Notifications;
import io.jmix.ui.exception.AbstractUiExceptionHandler;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

@Component("ui_tagTreeMovementTagHandler")
public class TagTreeMovementTagHandler extends AbstractUiExceptionHandler {

    public TagTreeMovementTagHandler() {
        super(TagTreeMovementTagException.class.getName());
    }

    @Override
    protected void doHandle(String className, String message, @Nullable Throwable throwable, UiContext context) {
        context.getNotifications().create(Notifications.NotificationType.WARNING).withCaption(message).show();
    }
}
