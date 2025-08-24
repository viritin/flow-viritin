package org.vaadin.firitin;

import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.Route;
import org.vaadin.firitin.components.badge.Badge;
import org.vaadin.firitin.components.orderedlayout.VVerticalLayout;

import static org.vaadin.firitin.components.badge.Badge.Theme.SUCCESS;

@Route
public class BadgeView extends VVerticalLayout {

    public BadgeView() {


        /* Core examples
        Span pending = new Span("Pending");
pending.getElement().getThemeList().add("badge");

Span confirmed = new Span("Confirmed");
confirmed.getElement().getThemeList().add("badge success");

Span denied = new Span("Denied");
denied.getElement().getThemeList().add("badge error");

Span onHold = new Span("On hold");
onHold.getElement().getThemeList().add("badge contrast");
         */
        add(new HorizontalLayout(){{
            add(new Badge("Pending"));
            add(new Badge("Confirmed").withTheme(SUCCESS));
            add(new Badge("Denied").withTheme(Badge.Theme.ERROR));
            add(new Badge("On hold").withTheme(Badge.Theme.CONTRAST));
        }});

        /*
        Core examples:
        // Icon before text
Span pending1 = new Span(createIcon(VaadinIcon.CLOCK),
        new Span("Pending"));
pending1.getElement().getThemeList().add("badge");

// Icon after text
Span pending2 = new Span(new Span("Pending"),
        createIcon(VaadinIcon.CLOCK));
pending2.getElement().getThemeList().add("badge");

...

private Icon createIcon(VaadinIcon vaadinIcon) {
    Icon icon = vaadinIcon.create();
    icon.getStyle().set("padding", "var(--lumo-space-xs)");
    return icon;
}

         */
        add(new Badge("Pending")
                .withTheme(Badge.Theme.PRIMARY)
                .withIcon(VaadinIcon.CLOCK)
        );

        add(new HorizontalLayout(){{
            add(new Badge("Confirmed")
                    .withTheme(SUCCESS)
                    .withIcon(VaadinIcon.CHECK)
            );
            add(new Badge("Denied")
                    .withTheme(Badge.Theme.ERROR)
                    .withIcon(VaadinIcon.EXCLAMATION_CIRCLE_O)
            );

            add(new Badge("On hold")
                    .withTheme(Badge.Theme.CONTRAST)
                    .withIcon(VaadinIcon.HAND)
            );

            add(new Badge("Pending").withIcon(VaadinIcon.CLOCK));
            add(new Badge("Confirmed").withTheme(SUCCESS).withIcon(VaadinIcon.CHECK));
            add(new Badge("Denied").withTheme(Badge.Theme.ERROR).withIcon(VaadinIcon.EXCLAMATION_CIRCLE_O));
            add(new Badge("On hold").withTheme(Badge.Theme.CONTRAST).withIcon(VaadinIcon.HAND));
        }});


        add(new HorizontalLayout(){{

            add(new Badge("Pending"));
            add(new Badge("Confirmed").withTheme(SUCCESS));
            add(new Badge("Denied").withTheme(Badge.Theme.ERROR));
            add(new Badge("On hold").withTheme(Badge.Theme.CONTRAST));
            add(new Badge("Pending").withTheme(Badge.Theme.PRIMARY));
            add(new Badge("Confirmed").withTheme(SUCCESS, Badge.Theme.PRIMARY));
            add(new Badge("Denied").withTheme(Badge.Theme.ERROR, Badge.Theme.PRIMARY));
            add(new Badge("On hold").withTheme(Badge.Theme.CONTRAST, Badge.Theme.PRIMARY));
        }});

        add(new HorizontalLayout(){{
            add(new Badge("Pending").withTheme(Badge.Theme.PILL));
            add(new Badge("Confirmed").withTheme(SUCCESS, Badge.Theme.PILL));
            add(new Badge("Denied").withTheme(Badge.Theme.ERROR, Badge.Theme.PILL));
            add(new Badge("On hold").withTheme(Badge.Theme.CONTRAST, Badge.Theme.PILL));
        }});

    }
}
