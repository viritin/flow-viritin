package org.vaadin.firitin.appframework;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HasText;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import org.vaadin.firitin.components.orderedlayout.VScroller;
import org.vaadin.firitin.util.style.VaadinCssProps;

import java.util.Map;
import java.util.Stack;
import java.util.WeakHashMap;

/**
 * Opinionated helpers and defaults for Vaadin {@link AppLayout}. Provides subviews and
 * a slot for view specific components in navbar (will be automatically cleaned up on navigation).
 */
public abstract class VAppLayout extends AppLayout implements AfterNavigationObserver {

    private AdvancedSideNav menu;
    private Footer drawerFooter;
    private H2 viewTitle;
    protected HorizontalLayout navbarHelpers;
    private Stack<Component> viewStack = new Stack<>();
    private Map<Component, String> explicitViewTitles = new WeakHashMap<>();
    private Scroller scroller = new VScroller();
    private boolean initialized;

    public VAppLayout() {
        setPrimarySection(Section.DRAWER);
    }

    /**
     * @return a {@link Component} to show as "drawer header" or a simple string.
     */
    protected abstract Object getDrawerHeader();

    protected void addHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.getElement().setAttribute("aria-label", "Menu toggle");

        addToNavbar(true, toggle, (Component) getViewTitle());
    }

    @Deprecated(forRemoval = true)
    protected AdvancedSideNav prepareNav() {
        return getMenu();
    }

    protected void addDrawerContent() {
        Object headerContent = getDrawerHeader();
        Component content;
        if (headerContent instanceof Component) {
            content = (Component) headerContent;
        } else {
            H1 appName = new H1(headerContent.toString());
            appName.getStyle().setMargin(VaadinCssProps.GAP_M.var());
            appName.getStyle().setFontSize("1.125rem");
            content = appName;
        }
        Header header = new Header(content);

        scroller.setContent(prepareNav());
        scroller.getStyle().setPadding(VaadinCssProps.GAP_S.var());

        addToDrawer(header, scroller, prepareDrawerFooter());
    }

    public AdvancedSideNav getMenu() {
        if(menu == null) {
            menu = new AdvancedSideNav();
        }
        return menu;
    }

    public Scroller getMenuScroller() {
        return scroller;
    }

    @Deprecated
    protected Footer prepareDrawerFooter() {
        return getDrawerFooter();
    }

    public Footer getDrawerFooter() {
        if(drawerFooter == null) {
            drawerFooter = new Footer();
        }
        return drawerFooter;
    }

    /**
     * Manually override the current view title.
     *
     * @param title the title to set
     */
    public void setViewTitle(String title) {
        getViewTitle().setText(title);
    }

    public HasText getViewTitle() {
        if(viewTitle == null) {
            viewTitle = new H2();
            viewTitle.getStyle().setFontSize("1.125rem");
            viewTitle.getStyle().setMargin("0");
        }
        return viewTitle;
    }

    protected HasComponents getNavbarHelpers() {
        if(navbarHelpers == null) {
            navbarHelpers = new HorizontalLayout();
            navbarHelpers.setAlignItems(FlexComponent.Alignment.BASELINE);
            navbarHelpers.getStyle().setPosition(Style.Position.ABSOLUTE);
            navbarHelpers.getStyle().setRight(VaadinCssProps.GAP_M.var());
            addToNavbar(true, navbarHelpers);
        }
        return navbarHelpers;
    }

    /**
     * Places a component by the view to the navbar. Components are placed to a div at the right edge.
     * The helper component is automatically cleared when navigating away from the view.
     *
     * @param component the helper component.
     */
    public void addNavbarHelper(Component component) {
        getNavbarHelpers().add(component);
    }

    protected void updateViewTitle() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < viewStack.size(); i++) {
            if (i > 0) {
                sb.append(" / ");
            }
            Component component = viewStack.get(i);
            if (explicitViewTitles.containsKey(component)) {
                sb.append(explicitViewTitles.get(component));
            } else {
                sb.append(NavigationItem.getMenuTextFromClass(component.getClass()));
            }
        }
        setViewTitle(sb.toString());
    }

    @Override
    public void setContent(Component content) {
        if(navbarHelpers != null) {
            navbarHelpers.removeAll();
        }
        while (viewStack.size() > 1) {
            closeSubView();
        }
        super.setContent(content);
        viewStack.clear();
        viewStack.push(content);
    }

    public void openSubView(Component component, String viewTitle) {
        viewStack.push(component);
        if (viewTitle != null) {
            explicitViewTitles.put(component, viewTitle);
        }
        super.setContent(component);
        updateViewTitle();
    }

    public void openSubView(Component component) {
        openSubView(component, null);
    }

    public void closeSubView(Component component) {
        Component pop = viewStack.pop();
        if (pop != component) {
            throw new IllegalStateException();
        }
        if (pop == null) {
            throw new IllegalStateException();
        }
        explicitViewTitles.remove(pop);
        super.setContent(viewStack.peek());
        updateViewTitle();
    }

    public void closeSubView() {
        Component pop = viewStack.pop();
        if (pop == null) {
            throw new IllegalStateException();
        }
        explicitViewTitles.remove(pop);
        super.setContent(viewStack.peek());
        updateViewTitle();
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        if(!initialized) {
            addDrawerContent();
            addHeaderContent();
            initialized = true;
        }
        updateViewTitle();
    }

}
