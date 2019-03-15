package org.vaadin.firitin.components.html;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Article;
import org.vaadin.firitin.fluency.ui.FluentClickNotifierWithoutTypedSource;
import org.vaadin.firitin.fluency.ui.FluentHtmlContainer;

public class VArticle extends Article implements FluentHtmlContainer<VArticle>, FluentClickNotifierWithoutTypedSource<VArticle> {

    public VArticle() {
        super();
    }

    public VArticle(Component... components) {
        super(components);
    }
}
