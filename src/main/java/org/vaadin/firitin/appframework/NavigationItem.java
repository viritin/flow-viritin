package org.vaadin.firitin.appframework;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.RouterLink;
import org.apache.commons.lang3.StringUtils;

/**
 * A component to represent a main view in the navigation menu
 */
public class NavigationItem extends SideNavItem {
    private final Class<? extends Component> navigationTarget;
    private final String text;
    private String path;
    private boolean disabled = false;

    public NavigationItem(Class<? extends Component> navigationTarget) {
        super(null, navigationTarget);
        getStyle().setDisplay(Style.Display.BLOCK); // TODO WTF?
        text = getMenuTextFromClass(navigationTarget);
        setLabel(text);
        MenuItem me = navigationTarget.getAnnotation(MenuItem.class);
        if (me != null) {
            if(me.icon() != null) {
                setPrefixComponent(new Icon(me.icon()));
            }
        }
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
    public void setPath(String path) {
        this.path = path;
        if(!disabled) {
            super.setPath(path);
        }
    }

    public void setEnabled(boolean enabled) {
        this.disabled = !enabled;
        if(disabled) {
            super.setPath((String) null);
        } else if (path != null) {
            super.setPath(path);
        }
        String color = enabled ? "" : "gray";
        getStyle().setColor(color);
    }

    public boolean isEnabled() {
        return !disabled;
    }
}
