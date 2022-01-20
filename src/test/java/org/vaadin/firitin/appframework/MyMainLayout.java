package org.vaadin.firitin.appframework;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.router.RoutePrefix;

@RoutePrefix("my-app")
public class MyMainLayout extends MainLayout {

	@Override
	protected String getDrawerHeader() {
		return "STS";
	}

}
