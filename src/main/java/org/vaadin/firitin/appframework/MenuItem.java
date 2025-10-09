package org.vaadin.firitin.appframework;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.icon.VaadinIcon;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface MenuItem {
    
    public static final int END = Integer.MAX_VALUE;
    public static final int BEGINNING = 0;
    public static final int DEFAULT = 1000;
    public static final Class<?> NO_PARENT = Class.class;

    /**
     * @return true if the menu item should be enabled in
     * the navigation. Note that making the view disabled with
     * this option, does NOT prevent navigation directly to
     * the view using deep linking url.
     */
	public boolean enabled() default true;

    public boolean hidden() default false;

	public String title() default "";
    
    public int order() default DEFAULT;
    
    public VaadinIcon icon() default VaadinIcon.FILE;

    /**
     * A URL for custom {@link com.vaadin.flow.component.icon.Icon} or {@link com.vaadin.flow.component.icon.SvgIcon}.
     * Takes a presence over {@link #icon()}.
     * @return URL for icon or empty string if not defined.
     */
    public String iconUrl() default "";

    /**
     * @return the parent view class in the menu hierarchy. Defaults to MenuItem.NO_PARENT
     * (Class.class) which means this is a top level view.
     */
    public Class<?> parent() default Class.class;

    /**
     * @return true if this (parent) view should be open by default in the menu
     */
    boolean openByDefault() default false;

    /**
     * @return true if this (parent) view should be collapsible in the menu (in case it is a grouping item)
     */
    boolean collapsible() default true;

}