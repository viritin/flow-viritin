package org.vaadin.firitin.util.webnotification;

import com.vaadin.flow.component.UI;

import java.util.concurrent.CompletableFuture;

public class WebNotification {

    private final UI ui;

    public static WebNotification get() {
        return new WebNotification(UI.getCurrent());
    }

    public WebNotification(UI ui) {
        this.ui = ui;
    }

    public void showNotification(String msg, NotificationOptions options) {
        ui.getPage().executeJs("""
            const options = %s;
            new Notification($0, options);
        """.formatted(options), msg);
    }
    public void showNotification(String msg) {
        ui.getPage().executeJs("""
            new Notification($0);
        """, msg);
    }

    public void showNotificationAsync(String msg) {
        ui.access(() -> showNotification(msg));
    }

    public void showNotificationAsync(String msg, NotificationOptions options) {
        ui.access(() -> showNotification(msg, options));
    }

    public enum Permission {
        DEFAULT, GRANTED, DENIED
    }

    public CompletableFuture<Permission> checkPermission() {
        return ui.getPage().executeJs("""
            return Notification.permission;
        """).toCompletableFuture(String.class).thenApply(str -> {
            return Permission.valueOf(str.toUpperCase());
        });
    }

    public void requestPermission(Runnable onSuccess, Runnable onError) {
        ui.getPage().executeJs("""
            return Notification.requestPermission();
        """).then(String.class, str -> {
            Permission permission = Permission.valueOf(str.toUpperCase());
            if (permission == Permission.GRANTED) {
                onSuccess.run();
            } else {
                onError.run();
            }
        }, error -> {
            // JavaScript error, not supported by browser or something else went wrong
            onError.run();
        });

    }

}
