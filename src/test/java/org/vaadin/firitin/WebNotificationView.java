package org.vaadin.firitin;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.markdown.Markdown;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.vaadin.firitin.components.button.VButton;
import org.vaadin.firitin.util.PageVisibility;
import org.vaadin.firitin.util.webnotification.NotificationOptions;
import org.vaadin.firitin.util.webnotification.WebNotification;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Route
public class WebNotificationView extends VerticalLayout {

    private final WebNotification webNotification;
    ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    public WebNotificationView() {
        add(new Markdown("""
                # Web Notification API Demo
                
                Vaadin has its built-in notification system, that shows a customisable notification within your browser. But it 
                can't notify the user when the browser is in the background.
                
                With Web Notification API, you can send notifications that will be displayed even when the browser is not in 
                focus, e.g. after your application has finished a long processing or the colleague of the user has submitted a 
                task. With these notifications the browser application uses OS's notification system to display the message.
                
                Vaadin Flow supports Web Push Notifications, which is a slightly thing, where the notifications
                are triggered via third party server (e.g. Apple, Google, Mozilla), even if a browser window is opened. This 
                feature/demo is about the using Notification's API directly in the browser, which is more limited, but easier to
                set up and use. These notifications are only shown when the browser window is still open.
                
                """));


        webNotification = WebNotification.get(); // Get the WebNotification instance, which bound to
        // the current UI. You can also use `WebNotification.get(yourUIInstance)` in more complex scenarios.

        // Check the permission status of Web Notifications, if not granted, request it,
        // else show the demo buttons to trigger notifications.
        webNotification.checkPermission().thenAccept(permission -> {
            if (permission == WebNotification.Permission.GRANTED) {
                Notification.show("Web Notification permission is already granted.");
                addDemoButtons();
                return;
            }

            if (permission == WebNotification.Permission.DENIED) {
                Notification.show("Web Notification permission is denied. You can change it in browser settings. Web" +
                        "app alone can't anymore request it.");
                add("Permission denied by the user. You can change it in browser settings. Web app alone can't request it anymore.");
            } else {
                Notification.show("Web Notification permission is default. You can request it to enable the demo.");
            }
            add(new VButton("requestPermission", e -> {
                webNotification.requestPermission(() -> {
                    Notification.show("Permissions granted. You can now try the demo buttons.");
                    addDemoButtons();
                    e.getSource().removeFromParent();
                }, () -> {
                    Notification.show("User denied permission.");
                });
            }));

        });
    }

    private void addDemoButtons() {
        add(new VButton("showNotification", e -> {
            webNotification.showNotification("Hello from Vaadin!");
        }));

        add(new VButton("showNotificationWithDelay (5 secs)", e -> {
            executorService.schedule(() -> {
                /*
                 * Note that this is happening in non UI thread, so modifying the UI directly
                 * is not allowed. Thus, we use `showNotificationAsync` method instead, which
                 * uses the `UI.access()` method to safely modify the UI. In case you are a big
                 * fan of spoiling your UI code with UI handling and UI.access() calls, you can
                 * also use the `webNotification.showNotification` method with the usual dance.
                 */
                webNotification.showNotificationAsync("Delayed notification from Vaadin!");
            }, 5, java.util.concurrent.TimeUnit.SECONDS);
        }));

        add(new VButton("notifications with 2 sec delay and custom settings", e -> {
            executorService.schedule(() -> {
                var options = new NotificationOptions();
                options.setBody("This is a custom notification body.");
                record StructuredData(String url, String status) {
                }
                // Note, MDN claims "data" is supported by all browsers, but I can't make it work in any 🤔
                options.setData(new StructuredData("https://vaadin.com", "success"));
                options.setSilent(false); // default varies by browser
                // There are many some options available, but many has browser differences, see the NotificationOptions class.
                webNotification.showNotificationAsync("Custom notification from Vaadin!", options);
            }, 2, java.util.concurrent.TimeUnit.SECONDS);
        }));

        add(new VButton("Web notification if page is visible and focused, else Vaadin Notification", e -> {
            UI current = UI.getCurrent();
            executorService.schedule(() -> {
                current.access(() -> {
                    PageVisibility.get().isVisible().thenAccept(visibility -> {
                        switch (visibility) {
                            case VISIBLE -> Notification.show("\"Normal\" Vaadin Notification, page: " + visibility);
                            // The page is visible and focused, so we use the regular Vaadin notification.
                            case VISIBLE_NON_FOCUSED, HIDDEN -> webNotification.showNotificationAsync("Web, page: " + visibility);
                        }
                    });
                });
            }, 2, java.util.concurrent.TimeUnit.SECONDS);
        }));


    }

    private void hookVisibilityChangeListener() {
        getElement().addEventListener("visibilitychange", event -> {
            if (getElement().getProperty("hidden", false)) {
                webNotification.showNotificationAsync("Browser tab is now hidden.");
            } else {
                webNotification.showNotificationAsync("Browser tab is now visible.");
            }
        });
    }

}
