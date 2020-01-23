package org.vaadin.firitin.components.html;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.ListItem;
import org.vaadin.firitin.fluency.ui.FluentClickNotifier;
import org.vaadin.firitin.fluency.ui.FluentHtmlContainer;

public class VListItem extends ListItem implements FluentHtmlContainer<VListItem>, FluentClickNotifier<ListItem, VListItem> {

    public VListItem() {
        super();
    }

    public VListItem(Component... components) {
        super(components);
    }

    public VListItem(String text) {
        super(text);
    }
}
