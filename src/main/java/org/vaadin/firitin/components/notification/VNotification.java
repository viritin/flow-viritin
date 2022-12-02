package org.vaadin.firitin.components.notification;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import org.vaadin.firitin.fluency.ui.FluentComponent;
import org.vaadin.firitin.fluency.ui.FluentHasComponents;
import org.vaadin.firitin.fluency.ui.FluentHasTheme;

public class VNotification extends Notification implements FluentComponent<VNotification>, FluentHasComponents<VNotification>, FluentHasTheme<VNotification> {

    public static final int DEFAULT_DURATION = 5000;

    public VNotification(String text) {
        super(text);
    }

    public VNotification(String text, int duration) {
        super(text, duration);
    }

    public VNotification(String text, int duration, Position position) {
        super(text, duration, position);
    }

    public VNotification(Component... components) {
        super(components);
    }

    public static VNotification show(String text, int duration, Notification.Position position) {
        VNotification notification = new VNotification(text, duration, position);
        notification.open();
        return notification;
    }

    public static VNotification show(String text, Notification.Position position) {
        return show(text, DEFAULT_DURATION, position);
    }

    public static VNotification show(String text) {
        return show(text, DEFAULT_DURATION, Notification.Position.BOTTOM_START);
    }

    public VNotification withText(String text) {
        setText(text);
        return this;
    }

    public VNotification withPosition(Position position) {
        setPosition(position);
        return this;
    }

    public VNotification withOpen() {
        open();
        return this;
    }

    public VNotification withDuration(int duration) {
        setDuration(duration);
        return this;
    }

    public VNotification withOpenedChangeListener(ComponentEventListener<OpenedChangeEvent> listener) {
        addOpenedChangeListener(listener);
        return this;
    }

    public VNotification withThemeVariants(NotificationVariant... variants) {
        addThemeVariants(variants);
        return this;
    }
}
