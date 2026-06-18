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
import java.util.Map;

@Configuration
public class Uihacker {

    static void adjustUITheme(UI ui, Class theme) {
        Object currentTheme = getUiTheme(ui);
        if(currentTheme == null && theme == null) {
            // use Lumo by default
            theme = Lumo.class;
        }

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
                boolean dark;
                if (testTheme != null) {
                    Class explicitTheme = testTheme.value();
                    setSessionTheme(session, explicitTheme);
                    adjustUITheme(uiEvt.getUI(), explicitTheme);
                    dark = false;
                } else {
                    Map<String, String> qp = parseQuery(realQueryParameters);
                    Class<?> qpTheme = readThemeFromQP(session, qp.get("theme"));
                    adjustUITheme(uiEvt.getUI(), qpTheme);
                    dark = isDark(qp.get("dark"));
                }
                adjustColorMode(uiEvt.getUI(), dark);
            });

        });
    }

    private Class<?> readThemeFromQP(VaadinSession session, String parameter) {
        // Note, as there is the "react router" and its "two-request init", reading
        // request.getParameter("theme") directly does not work; the original query
        // string is forwarded as the "query" parameter and parsed in parseQuery.
        Class<?> theme = null;

        if ("lumo".equals(parameter)) {
            theme = Lumo.class;
        } else if ("aura".equals(parameter)) {
            theme = Aura.class;
        } else if (parameter != null && !parameter.isEmpty()) {
            // anything that don't matched -> base
            theme = Void.class;
        }

        session.setAttribute("theme", theme);
        return theme;
    }

    /** Parses a raw query string (e.g. {@code theme=aura&dark}) into a map. */
    private static Map<String, String> parseQuery(String query) {
        Map<String, String> params = new java.util.HashMap<>();
        if (query == null || query.isEmpty()) {
            return params;
        }
        for (String pair : query.split("&")) {
            int eq = pair.indexOf('=');
            if (eq < 0) {
                params.put(pair, "");
            } else {
                params.put(pair.substring(0, eq), pair.substring(eq + 1));
            }
        }
        return params;
    }

    /**
     * Whether the {@code dark} query parameter requests dark mode. A bare
     * {@code ?dark}, {@code dark=true} or {@code dark=1} all enable it; absence
     * or {@code dark=false}/{@code 0} keep light mode.
     */
    private static boolean isDark(String value) {
        return value != null
                && (value.isEmpty() || value.equalsIgnoreCase("true") || value.equals("1"));
    }

    /**
     * Toggles dark mode on the document. Lumo's dark variant is driven by the
     * {@code theme="dark"} attribute on the root element, while Aura's tokens use
     * {@code light-dark()} and follow the CSS {@code color-scheme}. Setting both
     * covers either theme; clearing both returns to light mode.
     */
    static void adjustColorMode(UI ui, boolean dark) {
        if (dark) {
            ui.getPage().executeJs(
                    "document.documentElement.setAttribute('theme','dark');"
                            + "document.documentElement.style.colorScheme='dark';");
        } else {
            ui.getPage().executeJs(
                    "document.documentElement.removeAttribute('theme');"
                            + "document.documentElement.style.colorScheme='';");
        }
    }

    private void setSessionTheme(VaadinSession session, Class explicitTheme) {
        session.setAttribute("theme", explicitTheme);
        System.out.println("Changing theme to " + explicitTheme + " as view only supports it.");
    }

}