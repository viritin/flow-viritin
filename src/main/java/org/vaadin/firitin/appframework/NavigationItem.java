package org.vaadin.firitin.appframework;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.RouterLink;
import org.apache.commons.lang3.StringUtils;

public class NavigationItem extends Tab {
    private final Class<? extends Component> navigationTarget;
    private final String text;

    public NavigationItem(Class<? extends Component> navigationTarget) {
        super(new RouterLink("", navigationTarget));
        text = getMenuTextFromClass(navigationTarget);
        RouterLink rl = ((RouterLink) getChildren().findFirst().get());
        MenuItem me = navigationTarget.getAnnotation(MenuItem.class);
        if (me != null) {
            rl.add(new Icon(me.icon()));
        }
        rl.add(new Text(" " +text));

        this.navigationTarget = navigationTarget;
    }

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
