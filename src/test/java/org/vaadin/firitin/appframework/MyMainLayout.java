package org.vaadin.firitin.appframework;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.router.RoutePrefix;

@RoutePrefix("my-app")
public class MyMainLayout extends MainLayout {

	@Override
	protected Component[] getDrawerHeader() {
		return new Component[] {
				new Image("https://vaadin.com/images/trademark/PNG/VaadinLogomark_RGB_1000x1000.png", "My App logo"),
				new H1("STS")};
	}

}
