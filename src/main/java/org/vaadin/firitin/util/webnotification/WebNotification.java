package org.vaadin.firitin.util.webnotification;

import com.vaadin.flow.component.UI;

import java.util.concurrent.CompletableFuture;

/**
 * A utility class for displaying web notifications (aka native OS displayed notifications)
 * in a Vaadin application. It provides methods to show notifications, check permission status, and request permissions.
 *
 * Usage:
 * <pre>
 * WebNotification notification = WebNotification.get();
 * notification.showNotification("Hello World!");
 * </pre>
 * <p>
 * Compared to Vaadin's built-in notification system, this allows notifications to be displayed
 * even when the browser window is not in focus, using the native OS notification system.
 * Regular Vaadin notifications are more flexible for in-app notifications.
 * </p>
 */
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
            new Notification($0, $1);
        """, msg, options);
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
        """).toCompletableFuture(String.class).thenApply(str -> Permission.valueOf(str.toUpperCase()));
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
