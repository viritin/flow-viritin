package org.vaadin.firitin;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.shared.ui.LoadMode;
import com.vaadin.flow.theme.aura.Aura;
import com.vaadin.flow.theme.lumo.Lumo;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class Uihacker {

    static void adjustUITheme(UI ui, Class theme) {

        Object currentTheme = getUiTheme(ui);

        if (currentTheme != null && currentTheme == theme) {
            return;
        } else {
            List<Registration> registrations = (List<Registration>) ComponentUtil.getData(ui, "themereg");
            if (registrations == null) {
                registrations = new ArrayList<>();
            } else {
                registrations.forEach(r -> r.remove());
                registrations.clear();
            }

            // null defaults to Lumo
            if (theme == Lumo.class || theme == null) {
                registrations.add(ui.getPage().addStyleSheet(Lumo.STYLESHEET, LoadMode.EAGER));
                registrations.add(ui.getPage().addStyleSheet(Lumo.UTILITY_STYLESHEET, LoadMode.EAGER));
            } else if (theme == Aura.class) {
                registrations.add(ui.getPage().addStyleSheet(Aura.STYLESHEET, LoadMode.EAGER));
            }

            ComponentUtil.setData(ui, "themereg", registrations);
            ComponentUtil.setData(ui, "theme", theme);
        }
    }

    static Class<?> getUiTheme(UI ui) {
        Object currentTheme = ComponentUtil.getData(ui, "theme");
        return (Class<?>) currentTheme;
    }

    @EventListener
    private void onServiceInit(ServiceInitEvent serviceInitEvent) {
        serviceInitEvent.getSource().addUIInitListener(uiEvt -> {
            VaadinRequest request = VaadinRequest.getCurrent();
            VaadinSession session = uiEvt.getUI().getSession();


            // But Vaadin does some hack and sends the original query string as "query" query parameter...
            String realQueryParameters = request.getParameter("query");

            uiEvt.getUI().addAfterNavigationListener(event -> {
                Class<? extends Component> aClass = uiEvt.getUI().getCurrentView().getClass();
                TestTheme testTheme = aClass.getAnnotation(TestTheme.class);
                if (testTheme != null) {
                    Class explicitTheme = testTheme.value();
                    setSessionTheme(session, explicitTheme);
                    adjustUITheme(uiEvt.getUI(), explicitTheme);
                } else {
                    Class<?> qpTheme = readThemeFromQP(session, realQueryParameters);
                    adjustUITheme(uiEvt.getUI(), qpTheme);
                }
            });

        });
    }

    private Class<?> readThemeFromQP(VaadinSession session, String realQueryParameters) {
        // Note, as there is the "react router" and it "two-request init", this does not work!
            /*
            String parameter = request.getParameter("theme");
             */

        String parameter = "";
        String[] split = realQueryParameters == null ? new String[0] : realQueryParameters.split("=");
        for (int i = 0; i < split.length; i++) {
            if (split[i].equals("theme")) {
                parameter = split[i + 1];
                break;
            }
        }

        Class<?> theme = null;

        if ("lumo".equals(parameter)) {
            theme = Lumo.class;
        } else if ("aura".equals(parameter)) {
            theme = Aura.class;
        } else if (!parameter.isEmpty()) {
            // anything that don't matched -> base
            theme = Void.class;
        }

        session.setAttribute("theme", theme);
        return theme;
    }

    private void setSessionTheme(VaadinSession session, Class explicitTheme) {
        session.setAttribute("theme", explicitTheme);
        System.out.println("Changing theme to " + explicitTheme + " as view only supports it.");
    }

}