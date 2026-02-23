package org.vaadin.firitin;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.theme.aura.Aura;
import com.vaadin.flow.theme.lumo.Lumo;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class Uihacker {

    @EventListener
    private void onServiceInit(ServiceInitEvent serviceInitEvent) {
        serviceInitEvent.getSource().addUIInitListener(uiEvt -> {
            VaadinRequest request = VaadinRequest.getCurrent();
            VaadinSession session = uiEvt.getUI().getSession();

            uiEvt.getUI().addAfterNavigationListener(event -> {

                Class<? extends Component> aClass = uiEvt.getUI().getCurrentView().getClass();
                TestTheme testTheme = aClass.getAnnotation(TestTheme.class);
                if (testTheme != null) {
                    Class explicitTheme = testTheme.value();
                    session.setAttribute("theme", explicitTheme);
                    System.out.println("Changing theme to " + explicitTheme + " as view only supports it.");
                } else {

                    // Note, as there is the "react router" and it "two-request init", this does not work!
            /*
            String parameter = request.getParameter("theme");
             */

                    // But Vaadin does some hack and sends the original query string as "query" query parameter...
                    String realQueryParameters = request.getParameter("query");

                    String parameter = "";
                    String[] split = realQueryParameters == null ? new String[0] : realQueryParameters.split("=");
                    for (int i = 0; i < split.length; i++) {
                        if (split[i].equals("theme")) {
                            parameter = split[i + 1];
                            break;
                        }
                    }

                    if ("lumo".equals(parameter)) {
                        session.setAttribute("theme", Lumo.class);
                    } else if ("aura".equals(parameter)) {
                        session.setAttribute("theme", Aura.class);
                    } else if (!parameter.isEmpty()) {
                        // anything else (themeless)
                        session.setAttribute("theme", "void");
                    }
                }


                Object theme = session.getAttribute("theme");
                if (theme == null) {
                    theme = Lumo.class;
                }

                UI ui = uiEvt.getUI();


                List<Registration> registrations = (List<Registration>) ComponentUtil.getData(ui, "themereg");
                if(registrations == null) {
                    registrations = new ArrayList<>();
                } else {
                    registrations.forEach(r -> r.remove());
                    registrations.clear();
                }


                if (theme == Lumo.class) {
                    registrations.add(ui.getPage().addStyleSheet(Lumo.STYLESHEET));
                    registrations.add(ui.getPage().addStyleSheet(Lumo.UTILITY_STYLESHEET));
                } else if (theme == Aura.class) {
                    registrations.add(ui.getPage().addStyleSheet(Aura.STYLESHEET));
                }

                ComponentUtil.setData(ui, "themereg", registrations);

            });

        });
    }

}