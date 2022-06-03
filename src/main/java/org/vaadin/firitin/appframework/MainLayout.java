package org.vaadin.firitin.appframework;

import java.lang.reflect.Modifier;
import java.util.*;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.*;
import org.vaadin.firitin.components.orderedlayout.VHorizontalLayout;
import org.vaadin.firitin.util.style.Padding;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
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
@CssImport("./org/vaadin/firitin/layouts/appframework.css")
@CssImport("lumo-css-framework/all-classes.css")
@NpmPackage(value = "lumo-css-framework", version = "3.0.11")
public abstract class MainLayout extends AppLayout {

	private UnorderedList menu;

	private H1 viewTitle;

	private List<NavigationItem> navigationItems = new ArrayList<>();

	private Stack<Component> viewStack = new Stack<>();

	private Map<Component,String> explicitViewTitles = new WeakHashMap<>();

	public MainLayout() {
		getElement().getClassList().add("v-applayout");
	}

	@Override
	protected void onAttach(AttachEvent attachEvent) {
		init();
		super.onAttach(attachEvent);
	}

	protected void init() {
		// lazy init
		if(viewTitle != null) {
			return;
		}
		setPrimarySection(Section.DRAWER);
		addToNavbar(true, createHeaderContent());
		addToDrawer(createDrawerContent());
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
		navigationItems.stream().filter(this::checkAccess).forEach(menu::add);
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

	protected Component createHeaderContent() {
		DrawerToggle toggle = new DrawerToggle();
		toggle.addClassName("text-secondary");
		toggle.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
		toggle.getElement().setAttribute("aria-label", "Menu toggle");

		viewTitle = new H1();
		viewTitle.addClassNames("m-0", "text-l");

		Header header = new Header(toggle, viewTitle);
		header.addClassNames("bg-base", "border-b", "border-contrast-10", "box-border", "flex", "h-xl", "items-center",
				"w-full");
		return header;
	}

	protected Component createDrawerContent() {
		H2 appName = new H2(getDrawerHeader());
		appName.addClassNames("flex", "items-center", "h-xl", "m-0", "px-m", "text-m");
		com.vaadin.flow.component.html.Section section = new com.vaadin.flow.component.html.Section(
				appName,
				createNavigation(),
				createFooter());
		section.addClassNames("flex", "flex-col", "items-stretch", "max-h-full", "min-h-full");
		return section;
	}

	private Nav createNavigation() {
		Nav nav = new Nav();
		nav.addClassNames("border-b", "border-contrast-10", "flex-grow", "overflow-auto");
		nav.getElement().setAttribute("aria-labelledby", "views");

		// Wrap the links in a list; improves accessibility
		menu = new UnorderedList();
		menu.addClassNames("list-none", "m-0", "p-0");
		nav.add(menu);

		return nav;
	}

	protected abstract String getDrawerHeader();

	@Override
	protected void afterNavigation() {
		init();
		super.afterNavigation();
//		getNavigationItems().stream().filter(ni -> ni.getNavigationTarget().equals(getContent().getClass())).findFirst()
//				.ifPresent(ni -> menu.setSelectedTab(ni));
		updateViewTitle();
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
		layout.addClassNames("flex", "items-center", "my-s", "px-m", "py-xs");

		return layout;
	}


}
