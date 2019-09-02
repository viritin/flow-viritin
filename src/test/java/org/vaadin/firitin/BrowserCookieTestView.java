package org.vaadin.firitin;


import org.vaadin.firitin.components.button.VButton;
import org.vaadin.firitin.util.BrowserCookie;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.Route;
import java.time.LocalDateTime;
import org.vaadin.firitin.components.textfield.VTextField;

@Route(BrowserCookieTestView.ROUTE)
public class BrowserCookieTestView extends Div {
    public static final String ROUTE = "BrowserCookie";
    
    public BrowserCookieTestView() {
        
        VTextField value = new VTextField("Value to set");
        value.setValue(LocalDateTime.now().toString());
        
        VButton setCookie = new VButton().withText("Set Cookie").withClickListener(e -> {
            BrowserCookie.setCookie("test.cookie", value.getValue());
        });
        
        VButton detect = new VButton().withText("Detect").withClickListener(e -> {
            BrowserCookie.detectCookieValue("test.cookie", cookieValue -> {
                Notification.show(cookieValue);
            });
        });
        
        add(value, setCookie, detect);
    }
}
