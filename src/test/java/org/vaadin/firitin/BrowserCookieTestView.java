package org.vaadin.firitin;

import java.util.Date;

import org.vaadin.firitin.components.VButton;
import org.vaadin.firitin.util.BrowserCookie;
import org.vaadin.firitin.util.BrowserCookie.Callback;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.Route;

@Route(BrowserCookieTestView.ROUTE)
public class BrowserCookieTestView extends Div {
    public static final String ROUTE = "BrowserCookie";
    
    public BrowserCookieTestView() {
        VButton setCookie = new VButton().withText("Set Cookie").withClickListener(e -> {
            BrowserCookie.setCookie("test.cookie", new Date().toString());
            BrowserCookie.detectCookieValue("test.cookie", new Callback() {
                
                @Override
                public void onValueDetected(String value) {
                    Notification.show(value);
                }
            });
        });
        add(setCookie);
    }
}
