package org.vaadin.firitin;


import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.Route;
import org.vaadin.firitin.components.button.VButton;
import org.vaadin.firitin.components.textfield.VTextField;
import org.vaadin.firitin.util.BrowserCookie;
import org.vaadin.firitin.util.WebStorage;

import java.time.LocalDateTime;

@Route
public class WebStorageTestView extends Div {

    public WebStorageTestView() {
        
        VTextField value = new VTextField("Value to set");
        value.setValue(LocalDateTime.now().toString());
        
        VButton setData = new VButton().withText("Set value").withClickListener(e -> {
            WebStorage.setItem("test", value.getValue());
        });
        
        VButton detect = new VButton().withText("Detect").withClickListener(e -> {
            WebStorage.getItem("test", v -> Notification.show(v));
        });
        
        add(value, setData, detect);

    }
}
