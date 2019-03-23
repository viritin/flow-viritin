package org.vaadin.firitin.components.html;

import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.OrderedList;
import org.vaadin.firitin.fluency.ui.FluentClickNotifier;
import org.vaadin.firitin.fluency.ui.FluentHtmlContainer;

public class VOrderedList extends OrderedList implements FluentHtmlContainer<VOrderedList>, FluentClickNotifier<OrderedList, VOrderedList> {

    public VOrderedList() {
        super();
    }

    public VOrderedList(OrderedList.NumberingType type) {
        super(type);
    }

    public VOrderedList(ListItem... items) {
        super(items);
    }

    public VOrderedList withType(OrderedList.NumberingType type) {
        setType(type);
        return this;
    }

}
