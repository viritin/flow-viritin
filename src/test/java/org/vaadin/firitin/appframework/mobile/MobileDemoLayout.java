package org.vaadin.firitin.appframework.mobile;

import com.vaadin.flow.router.RoutePrefix;
import com.vaadin.flow.theme.aura.Aura;
import com.vaadin.flow.theme.lumo.Lumo;
import org.vaadin.firitin.TestTheme;
import org.vaadin.firitin.appframework.MobileMainLayout;
import org.vaadin.firitin.appframework.NavigationItem;

/**
 * Demo of {@link MobileMainLayout}. Open on a narrow viewport (or with the
 * browser dev tools device toolbar) to see the bottom icon navigation; on a
 * wide window it falls back to the regular drawer.
 */
@RoutePrefix("mobile")
public class MobileDemoLayout extends MobileMainLayout {

    public MobileDemoLayout() {
        // (Body/page scrolling is on by default.)
        // Demonstrate the optional captions under the bottom-bar icons.
        setBottomNavLabelsVisible(false);
        // The in-content title is off by default; show it here to demonstrate the
        // scroll-away heading.
        setViewTitleVisible(false);
    }

    @Override
    protected String getDrawerHeader() {
        return "Mobile demo";
    }

    @Override
    protected boolean checkAccess(NavigationItem item) {
        // Keep this demo's menu to its own views (the auto-scan otherwise picks
        // up every view bound to any MainLayout subclass in the test sources).
        return item.getNavigationTarget().getPackageName()
                .startsWith(getClass().getPackageName());
    }
}
