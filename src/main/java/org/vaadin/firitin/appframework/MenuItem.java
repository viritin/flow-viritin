package org.vaadin.firitin.appframework;

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

    /**
     * @return true if the menu item should be enabled in
     * the navigation. Note that making the view disabled with
     * this option, does NOT prevent navigation directly to
     * the view using deep linking url.
     */
	public boolean enabled() default true;
	
	public String title() default "";
    
    public int order() default DEFAULT;
    
    public VaadinIcon icon() default VaadinIcon.FILE;

}