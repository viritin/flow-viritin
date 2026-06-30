package org.vaadin.firitin.appframework.caf;

import com.vaadin.flow.router.RoutePrefix;
import org.vaadin.firitin.appframework.MobileMainLayout;
import org.vaadin.firitin.appframework.NavigationItem;

/**
 * Demo of a two-level hierarchy on {@link MobileMainLayout}: the "Weather
 * history" group's sub-views open from a popover in the bottom bar, without a
 * "More" item or the drawer. Mirrors the caf-hierarchical-menu mockup.
 */
@RoutePrefix("caf")
public class CafLayout extends MobileMainLayout {

    public CafLayout() {
        setBottomNavLabelsVisible(true);
    }

    @Override
    protected String getDrawerHeader() {
        return "CaF";
    }

    @Override
    protected boolean checkAccess(NavigationItem item) {
        // Keep this demo's menu to its own views (the auto-scan otherwise picks up
        // every view bound to any MainLayout subclass in the test sources).
        return item.getNavigationTarget().getPackageName()
                .startsWith(getClass().getPackageName());
    }
}
