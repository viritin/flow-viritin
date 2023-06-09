package org.vaadin.firitin.appframework;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.router.RoutePrefix;

@RoutePrefix("my-app")
public class MyMainLayout extends MainLayout {

	public MyMainLayout() {
		super();
	}

	@Override
	protected String getDrawerHeader() {
		return "STS";
	}

	@Override
	protected void onAttach(AttachEvent attachEvent) {
		super.onAttach(attachEvent);
		getNavigationItems().get(3).setEnabled(false);
	}
}
