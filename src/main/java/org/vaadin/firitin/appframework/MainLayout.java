package org.vaadin.firitin.appframework;

import java.lang.reflect.Modifier;
import java.util.*;

import org.vaadin.firitin.components.orderedlayout.VHorizontalLayout;
import org.vaadin.firitin.util.style.Padding;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
import com.vaadin.flow.router.RouteBaseData;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.router.RouterLayout;

/**
 * The main view is a top-level placeholder for other views.
 */
@CssImport("./org/vaadin/firitin/layouts/appframework.css")
@CssImport("lumo-css-framework/all-classes.css")
@NpmPackage(value = "lumo-css-framework", version = "3.0.11")
public abstract class MainLayout extends AppLayout {

	private final Tabs menu;
	private H1 viewTitle;

	private List<NavigationItem> navigationItems = new ArrayList<>();

	private Stack<Component> viewStack = new Stack<>();

	private Map<Component,String> explicitViewTitles = new WeakHashMap<>();

	public MainLayout() {
		setPrimarySection(Section.DRAWER);
		addToNavbar(true, createHeaderContent());
		menu = createMenu();
		addToDrawer(createDrawerContent(menu));
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
				if (!Modifier.isAbstract(routeClass.getModifiers())) {
					navigationItems.add(new NavigationItem(routeClass));
				}
			});
			sortMenuItems();
			buildMenu();
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
		navigationItems.forEach(menu::add);
	}

	protected Component createHeaderContent() {
		VHorizontalLayout layout = new VHorizontalLayout().withPadding(Padding.Side.RIGHT);
		layout.setId("header");
		layout.getThemeList().set("dark", true);
		layout.setWidthFull();
		layout.setSpacing(false);
		layout.setAlignItems(FlexComponent.Alignment.CENTER);
		layout.add(new DrawerToggle());
		viewTitle = new H1();
		layout.add(viewTitle);
		layout.addAndExpand(viewTitle);
		return layout;
	}

	protected Component createDrawerContent(Tabs menu) {
		VerticalLayout layout = new VerticalLayout();
		layout.setSizeFull();
		layout.setPadding(false);
		layout.setSpacing(false);
		layout.getThemeList().set("spacing-s", true);
		layout.setAlignItems(FlexComponent.Alignment.STRETCH);
		HorizontalLayout logoLayout = new HorizontalLayout();
		logoLayout.setId("logo");
		logoLayout.setAlignItems(FlexComponent.Alignment.CENTER);
		logoLayout.add(getDrawerHeader());
		layout.add(logoLayout, menu);
		return layout;
	}

	protected abstract Component[] getDrawerHeader();

	private Tabs createMenu() {
		final Tabs tabs = new Tabs();
		tabs.setOrientation(Tabs.Orientation.VERTICAL);
		tabs.addThemeVariants(TabsVariant.LUMO_MINIMAL);
		tabs.setId("tabs");
		return tabs;
	}

	@Override
	protected void afterNavigation() {
		super.afterNavigation();
		getNavigationItems().stream().filter(ni -> ni.getNavigationTarget().equals(getContent().getClass())).findFirst()
				.ifPresent(ni -> menu.setSelectedTab(ni));
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
		viewStack.push(component);
		super.setContent(component);
	}

	public void closeSubView(Component component) {
		Component pop = viewStack.pop();
		if(pop != component) {
			throw new IllegalStateException();
		}
		if(pop == null) {
			throw new IllegalStateException();
		}
		super.setContent(viewStack.peek());
		updateViewTitle();
	}

	public void closeSubView() {
		Component pop = viewStack.pop();
		if(pop == null) {
			throw new IllegalStateException();
		}
		super.setContent(viewStack.peek());
		updateViewTitle();
	}

}
