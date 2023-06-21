package org.vaadin.firitin.appframework;

import java.lang.reflect.Modifier;
import java.util.*;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.theme.lumo.LumoUtility;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.router.RouteBaseData;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.router.RouterLayout;

/**
 * The main view is a top-level placeholder for other views. This version is based on a one
 * produced by start.vaadin.com service, but add a bit structure to it and populates
 * main views automatically to it.
 * <p>Suites as such for small apps that has no special needs for the main layout.
 * Menu items can be configured (override caption, order, icon) using {@link MenuItem} annotation</p>
 * <p>Check usage example from the text package org.vaadin.firitin.appframework</p>
 */
public abstract class MainLayout extends AppLayout implements AfterNavigationObserver {

	private H2 viewTitle;

	public MainLayout() {
//		getElement().getClassList().add("v-applayout");
		setPrimarySection(Section.DRAWER);
		addDrawerContent();
		addHeaderContent();
	}

	private void addHeaderContent() {
		DrawerToggle toggle = new DrawerToggle();
		toggle.getElement().setAttribute("aria-label", "Menu toggle");

		viewTitle = new H2();
		viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);

		addToNavbar(true, toggle, viewTitle);
	}

	private void addDrawerContent() {
		H1 appName = new H1(getDrawerHeader());
		appName.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.MEDIUM);
		Header header = new Header(appName);

		Scroller scroller = new Scroller(prepareNav());
		scroller.addClassNames(LumoUtility.Padding.SMALL);

		addToDrawer(header, scroller, prepareFooter());
	}

	protected SideNav prepareNav() {
		// SideNav is a production-ready official component under a feature flag.
		// However, it has accessibility issues and is missing some features.
		// Both will be addressed in an upcoming minor version.
		// These changes are likely to cause some breaking change to the custom css
		// applied to the component.
		SideNav nav = new SideNav();
		this.menu = nav;
		return nav;
	}

	private Footer prepareFooter() {
		Footer layout = new Footer();
		return layout;
	}

	private SideNav menu;

	private List<NavigationItem> navigationItems = new ArrayList<>();

	private Stack<Component> viewStack = new Stack<>();

	private Map<Component,String> explicitViewTitles = new WeakHashMap<>();

	@Override
	protected void onAttach(AttachEvent attachEvent) {
		if(navigationItems.isEmpty()) {
			init();
		}
		super.onAttach(attachEvent);
	}

	protected void init() {
		RouteConfiguration.forSessionScope().getAvailableRoutes().stream().filter(routeData -> {
			Class<? extends RouterLayout> parentLayout = routeData.getParentLayout();
			if (parentLayout != null) {
				boolean assignableFrom = MainLayout.class.isAssignableFrom(parentLayout);
				return assignableFrom;
			}
			return false;
		}).forEach(rd -> {
			Class<? extends Component> routeClass = rd.getNavigationTarget();
			if (!Modifier.isAbstract(routeClass.getModifiers())) {
				navigationItems.add(new NavigationItem(routeClass));
			}
		});

		// Add and remove dynamically added routes
		RouteConfiguration.forApplicationScope().addRoutesChangeListener(event -> {
			event.getRemovedRoutes().forEach(route -> {
				Iterator<NavigationItem> iterator = navigationItems.iterator();
				while (iterator.hasNext()) {
					NavigationItem item = iterator.next();
					if (item.getNavigationTarget() == route.getNavigationTarget()) {
						iterator.remove();
					}
				}
			});
			// UI access used to support reload by JRebel etc
			MainLayout.this.getUI().ifPresent(ui -> ui.access(()->{
				List<RouteBaseData<?>> addedRoutes = event.getAddedRoutes();
				addedRoutes.stream().filter(routeData -> {
					Class<? extends RouterLayout> parentLayout = routeData.getParentLayout();
					if (parentLayout != null) {
						boolean assignableFrom = MainLayout.class.isAssignableFrom(parentLayout);
						return assignableFrom;
					}
					return false;
				}).forEach(rd -> {
					Class<? extends Component> routeClass = rd.getNavigationTarget();
					if (!Modifier.isAbstract(routeClass.getModifiers()) && routeClass != null) {
						navigationItems.add(new NavigationItem(routeClass));
					}
				});
				sortMenuItems();
				buildMenu();
			}));
		});

		sortMenuItems();

		buildMenu();
	}

	protected void sortMenuItems() {

		Collections.sort(navigationItems, new Comparator<NavigationItem>() {

			@Override
			public int compare(NavigationItem o1, NavigationItem o2) {
				MenuItem a1 = o1.getNavigationTarget().
						getAnnotation(MenuItem.class);
				MenuItem a2 = o2.getNavigationTarget().
						getAnnotation(MenuItem.class);
				if (a1 == null && a2 == null) {
					return o1.getText().compareTo(o2.getText());
				} else {
					int order1 = a1 == null ? MenuItem.DEFAULT : a1.order();
					int order2 = a2 == null ? MenuItem.DEFAULT : a2.order();
					if (order1 == order2) {
						return o1.getText().compareTo(o2.getText());
					} else {
						return order1 - order2;
					}
				}
			}
		});
	}

	/**
	 * @return A List of {@link NavigationItem} objects to be shown in the menu.
	 *         After modifying these manually, call {@link #buildMenu()} to update
	 *         the screen.
	 */
	public List<NavigationItem> getNavigationItems() {
		if(navigationItems.isEmpty()) {
			init();
		}
		return navigationItems;
	}

	/**
	 * This method can be called to re-build the menu, if e.g. views has been added,
	 * removed or otherwise changed.
	 * 
	 * If you have dynamically added/removed views from another thread, wrap the
	 * behavior in UI.access method.
	 */
	public void buildMenu() {
		menu.removeAll();
		navigationItems.stream().filter(this::checkAccess).forEach(menu::addItem);
	}

	/**
	 * Application that has access control can limit the appearance of the
	 * navigation item in the menu by returning false here.
	 *
	 * @param navigationItem the navigation item
	 * @return true if item should be visible or not
	 */
	protected boolean checkAccess(NavigationItem navigationItem) {
		return true;
	}

	protected abstract String getDrawerHeader();

	@Override
	protected void afterNavigation() {
		super.afterNavigation();
		updateViewTitle();
		updateSelectedNavigationItem();
	}

	@Override
	public void afterNavigation(AfterNavigationEvent event) {
		updateViewTitle();
		updateSelectedNavigationItem();
	}

	private void updateSelectedNavigationItem() {
		getNavigationItems().forEach(i -> {
			if(i.getNavigationTarget() == getContent().getClass()) {
				i.getElement().setAttribute("active", true);
			} else {
				i.getElement().removeAttribute("active");
			}
		});
	}

	private void updateViewTitle() {
		StringBuilder sb = new StringBuilder();
		for(int i = 0 ; i < viewStack.size(); i++) {
			if(i > 0) {
				sb.append(" / ");
			}
			Component component = viewStack.get(i);
			if(explicitViewTitles.containsKey(component)) {
				sb.append(explicitViewTitles.get(component));
			} else {
				sb.append(NavigationItem.getMenuTextFromClass(component.getClass()));
			}
		}
		viewTitle.setText(sb.toString());
	}

	@Override
	public void setContent(Component content) {
		while(viewStack.size() > 1) {
			closeSubView();
		}
		super.setContent(content);
		viewStack.clear();
		viewStack.push(content);
	}

	public void openSubView(Component component, String viewTitle) {
		viewStack.push(component);
		if(viewTitle != null) {
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
		if(pop != component) {
			throw new IllegalStateException();
		}
		if(pop == null) {
			throw new IllegalStateException();
		}
		explicitViewTitles.remove(pop);
		super.setContent(viewStack.peek());
		updateViewTitle();
	}

	public void closeSubView() {
		Component pop = viewStack.pop();
		if(pop == null) {
			throw new IllegalStateException();
		}
		explicitViewTitles.remove(pop);
		super.setContent(viewStack.peek());
		updateViewTitle();
	}

	protected Footer createFooter() {
		Footer layout = new Footer();
		return layout;
	}

}
