package org.vaadin.firitin.appframework;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.RoutePrefix;
import org.vaadin.firitin.util.VStyleUtil;

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
        //getNavigationItems().get(3).setEnabled(false);

        SideNavItem customItem = new SideNavItem(null, "/");
        Div div = new Div("Show all Viritin tests and this item is freaking long");
        customItem.setPrefixComponent(div);
        getMenu().addItem(customItem);

    }
}
