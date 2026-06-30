package org.vaadin.firitin.appframework.caf;

import com.vaadin.flow.component.icon.VaadinIcon;
import org.vaadin.firitin.appframework.MenuItem;

/**
 * A non-routable grouping marker: the weather-history sub-views point to this
 * with {@code @MenuItem(parent = WeatherHistory.class)}, which makes it a group
 * (a popover in the bottom bar, a sub-menu in the drawer).
 */
@MenuItem(title = "Weather history", icon = VaadinIcon.CLOUD, order = 20, openByDefault = true)
public class WeatherHistory {
}
