package org.vaadin.firitin.appframework;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
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
import com.vaadin.flow.router.PageTitle;
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
				navigationItems.add(new NavigationItem(getMenuTitle(routeClass), routeClass));
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
					navigationItems.add(new NavigationItem(getMenuTitle(routeClass), routeClass));
				}
			});
			sortMenuItems();
			buildMenu();
		});

		sortMenuItems();

		buildMenu();
	}

	protected void sortMenuItems() {
		navigationItems.sort((o1, o2) -> o1.getText().compareTo(o2.getText()));
	}

	protected String getMenuTitle(Class<? extends Component> routeClass) {
		return detectTitleFromAnnotationOrClassName(routeClass);
	};

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
		viewTitle.setText(getCurrentPageTitle());
	}

	protected String getCurrentPageTitle() {
		Class<? extends Component> clazz = getContent().getClass();
		return detectTitleFromAnnotationOrClassName(clazz);
	}

	private String detectTitleFromAnnotationOrClassName(Class<? extends Component> clazz) {
		PageTitle title = clazz.getAnnotation(PageTitle.class);
		if (title == null) {
			String simpleName = clazz.getSimpleName();
			if (simpleName.endsWith("View")) {
				simpleName = simpleName.substring(0, simpleName.length() - 4);
			}
			return StringUtils.join(StringUtils.splitByCharacterTypeCamelCase(simpleName), ' ');
		}
		return title == null ? "" : title.value();
	}

}
