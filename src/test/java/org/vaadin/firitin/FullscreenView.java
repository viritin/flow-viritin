package org.vaadin.firitin;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.markdown.Markdown;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.page.Page;
import com.vaadin.flow.router.Route;
import org.vaadin.firitin.components.orderedlayout.VVerticalLayout;
import org.vaadin.firitin.layouts.HorizontalFloatLayout;
import org.vaadin.firitin.util.fullscreen.FullScreen;

@Route
public class FullscreenView extends VVerticalLayout {

    private HorizontalFloatLayout buttons;

    public FullscreenView() {
        add(new Markdown("""
                # Fullscreen demo
                
                This view demonstrates how to use the FullScreen utility class to request full screen mode in a Vaadin application.
                
                """));

        Page page = UI.getCurrent().getPage();

        buttons = new HorizontalFloatLayout() {{
            add(new Button("Fullscreen.fullScreenAvailable()", e -> {
                // Check if the current UI is in full screen mode
                FullScreen.fullScreenAvailable().thenAccept(available -> {
                    if (available) {
                        Notification.show("The browser supports Fullscreen mode");
                    } else {
                        Notification.show("The full screen mode is not available for a reason or another (like beeing an iPhone :-) ).");
                    }
                });
            }));


            add(new Button("Fullscreen.requestFullscreen()", e -> {
                // Request full screen for the current UI
                FullScreen.requestFullscreen();
            }));

            add(new Button("Fullscreen.requestFullscreen(buttons)", e -> {
                // Request full screen for the current UI
                FullScreen.requestFullscreen(buttons);
            }));

            add(new Button("Fullscreen.exitFullscreen()", e -> {
                // Exit full screen mode
                FullScreen.exitFullscreen();
            }));

            add(new Button("Fullscreen.isFullscreen()", e -> {
                // Check if the current UI is in full screen mode
                FullScreen.isFullscreen().thenAccept(isFullScreen -> {
                    if (isFullScreen) {
                        Notification.show("The application is currently in full screen mode.");
                    } else {
                        Notification.show("The application is not in full screen mode.");
                    }
                });
            }));


            add(new Button("Listen change events", e-> {
                FullScreen.addFullscreenChangeListener(event -> {
                    if (event.isFullscreen()) {
                        Notification.show("Entered full screen mode.");
                    } else {
                        Notification.show("Exited full screen mode.");
                    }
                });
            }));

        }};
        add(buttons);

    }
}
