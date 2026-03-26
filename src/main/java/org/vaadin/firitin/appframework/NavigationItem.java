package org.vaadin.firitin.appframework;

import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.shared.util.SharedUtil;

import java.lang.annotation.Annotation;

public interface NavigationItem extends HasStyle {
    /**
     * Detects a menu item text for a view class, based on various annotations and falling back to genearing one from
     * the class name.
     *
     * @param navigationTarget the view class
     * @return string used in the menu/breadcrump for the view
     */
    static String getMenuTextFromClass(Class<?> navigationTarget) {
        final String text;
        MenuItem me = getAnnotationFromType(navigationTarget, MenuItem.class);
        if (me != null && !me.title().isEmpty()) {
            text = me.title();
        } else {
            Menu menu = getAnnotationFromType(navigationTarget, Menu.class);
            if (menu != null && !menu.title().isEmpty()) {
                text = menu.title();
            } else {
                PageTitle title = getAnnotationFromType(navigationTarget, PageTitle.class);
                if (title == null) {
                    String simpleName = navigationTarget.getSimpleName();
                    // weld proxy
                    if (simpleName.endsWith("_Subclass")) {
                        simpleName = simpleName.substring(0, simpleName.length() - "_Subclass".length());
                    }
                    if (simpleName.endsWith("View")) {
                        simpleName = simpleName.substring(0, simpleName.length() - 4);
                    }
                    text = SharedUtil.camelCaseToHumanFriendly(simpleName);
                } else {
                    text = title.value();
                }
            }
        }
        return text;
    }

    static <A extends Annotation> A getAnnotationFromType(Class<?> classType, Class<A> annotationClass) {
        while (!classType.getName().equals(Object.class.getName())) {

            if (classType.isAnnotationPresent(annotationClass)) {
                return classType.getAnnotation(annotationClass);
            }
            classType = classType.getSuperclass();
        }
        return null;
    }

    String getText();

    void setLabel(String label);

    Class<?> getNavigationTarget();

    boolean isEnabled();

    void setActive(boolean active);

    void addSubItem(NavigationItem item);

    void setParentItem(NavigationItem basicNavigationItem);
    NavigationItem getParentItem();
}
