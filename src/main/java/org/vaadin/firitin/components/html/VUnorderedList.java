package org.vaadin.firitin.components.html;

import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.UnorderedList;
import org.vaadin.firitin.fluency.ui.FluentClickNotifier;
import org.vaadin.firitin.fluency.ui.FluentHtmlContainer;

public class VUnorderedList extends UnorderedList implements FluentHtmlContainer<VUnorderedList>, FluentClickNotifier<UnorderedList, VUnorderedList> {

    public VUnorderedList() {
        super();
    }

    public VUnorderedList(ListItem... items) {
        super(items);
    }

}
