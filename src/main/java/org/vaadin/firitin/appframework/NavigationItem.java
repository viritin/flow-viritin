package org.vaadin.firitin.appframework;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.RouterLink;
import org.apache.commons.lang3.StringUtils;

/**
 * A component to reprecent an main view in the navigation menu
 */
public class NavigationItem extends ListItem {
    private final Class<? extends Component> navigationTarget;
    private final String text;

    public NavigationItem(Class<? extends Component> navigationTarget) {
        super(new RouterLink("", navigationTarget));
        text = getMenuTextFromClass(navigationTarget);
        RouterLink rl = ((RouterLink) getChildren().findFirst().get());
        // Don't ask what these do, copied from start.vaadin.com template
        rl.addClassNames("flex", "mx-s", "p-s", "relative", "text-secondary");
        MenuItem me = navigationTarget.getAnnotation(MenuItem.class);
        if (me != null) {
            // TODO figure out how to make icon type configurable, because apparently
            // the cool kids don't use Icon class any more.
            rl.add(new Icon(me.icon()));
        }
        // TODO figure out how to let users intercept this for i18n
        Span text = new Span(this.text);
        text.addClassNames("font-medium", "text-s");
        rl.add(text);

        this.navigationTarget = navigationTarget;
    }

    /**
     * Detects a menu item text for a view class, based on various annotations and falling back to genearing one from
     * the class name.
     * @param navigationTarget the view class
     * @return string used in the menu/breadcrump for the view
     */
    public static String getMenuTextFromClass(Class<? extends Component> navigationTarget) {
        final String text;
        MenuItem me = navigationTarget.getAnnotation(MenuItem.class);
        if (me != null && !me.title().isEmpty()) {
            text = me.title();
        } else {
            PageTitle title = navigationTarget.getAnnotation(PageTitle.class);
            if (title == null) {
                String simpleName = navigationTarget.getSimpleName();
                if (simpleName.endsWith("View")) {
                    simpleName = simpleName.substring(0, simpleName.length() - 4);
                }
                text = StringUtils.join(StringUtils.splitByCharacterTypeCamelCase(simpleName), ' ');
            } else {
                text = title.value();
            }
        }
        return text;
    }

    public String getText() {
        return text;
    }

    public Class<? extends Component> getNavigationTarget() {
        return navigationTarget;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        String color = enabled ? "" : "gray";
        getStyle().set("color", color);
    }
}
