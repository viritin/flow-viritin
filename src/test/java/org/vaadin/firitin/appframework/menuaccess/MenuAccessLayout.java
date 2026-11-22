package org.vaadin.firitin.appframework.menuaccess;

import java.util.ArrayList;
import java.util.List;

import org.vaadin.firitin.appframework.MainLayout;
import org.vaadin.firitin.appframework.NavigationItem;

/** Denies the {@link Secret} views, as role-based access control would. */
public class MenuAccessLayout extends MainLayout {

    private List<NavigationItem> topLevel = List.of();

    @Override
    protected boolean checkAccess(NavigationItem item) {
        return !item.getNavigationTarget().isAnnotationPresent(Secret.class);
    }

    @Override
    protected void onMenuBuilt(List<NavigationItem> topLevelItems) {
        topLevel = topLevelItems;
    }

    /** The menu as shown: "Title" for a top-level item, "Group > Title" for one in a group. */
    List<String> shownMenu() {
        List<String> shown = new ArrayList<>();
        for (NavigationItem item : topLevel) {
            shown.add(item.getText());
            getChildItems(item).forEach(child -> shown.add(item.getText() + " > " + child.getText()));
        }
        return shown;
    }

    @Override
    protected String getDrawerHeader() {
        return "Menu access";
    }
}
