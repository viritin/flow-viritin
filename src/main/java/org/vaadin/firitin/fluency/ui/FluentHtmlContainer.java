package org.vaadin.firitin.fluency.ui;

public interface FluentHtmlContainer<S extends FluentHasComponents<S>& FluentHasText<S> & FluentHasStyle<S> & FluentHasSize<S> & FluentComponent<S>>
        extends FluentHasComponents<S>, FluentHasText<S>, FluentHasStyle<S>, FluentHasSize<S>, FluentComponent<S>  {

}
