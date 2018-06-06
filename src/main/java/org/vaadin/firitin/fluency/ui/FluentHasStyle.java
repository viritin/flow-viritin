package org.vaadin.firitin.fluency.ui;

import com.vaadin.flow.component.HasStyle;

@SuppressWarnings("unchecked")
public interface FluentHasStyle<S extends FluentHasStyle<S>> extends HasStyle {

    // Javadoc copied form Vaadin Framework

    /**
     * Changes the primary style name of the component.
     *
     * <p>
     * The primary style name identifies the component when applying the CSS
     * theme to the Component. By changing the style name all CSS rules targeted
     * for that style name will no longer apply, and might result in the
     * component not working as intended.
     * </p>
     *
     * @param className The new primary style name
     * @return this (for method chaining)
     * @see #setClassName(String)
     */
    default S withPrimaryClassName(String className) {
        setClassName(className);
        return (S) this;
    }

    // Javadoc copied form Vaadin Framework

    /**
     * Sets one or more user-defined style names of the component, replacing any
     * previous user-defined styles. Multiple styles can be specified as a
     * space-separated list of style names. The style names must be valid CSS
     * class names and should not conflict with any built-in style names in
     * Vaadin or GWT.
     *
     * <pre>
     * Label label = new Label(&quot;This text has a lot of style&quot;);
     * label.setStyleName(&quot;myonestyle myotherstyle&quot;);
     * </pre>
     *
     * <p>
     * Each style name will occur in two versions: one as specified and one that
     * is prefixed with the style name of the component. For example, if you
     * have a {@code Button} component and give it "{@code mystyle}" style, the
     * component will have both "{@code mystyle}" and "{@code v-button-mystyle}"
     * styles. You could then style the component either with:
     * </p>
     *
     * <pre>
     * .myonestyle {background: blue;}
     * </pre>
     *
     * <p>
     * or
     * </p>
     *
     * <pre>
     * .v-button-myonestyle {background: blue;}
     * </pre>
     *
     * <p>
     * It is normally a good practice to use {@link #addClassName(String)
     * addStyleName()} rather than this setter, as different software
     * abstraction layers can then add their own styles without accidentally
     * removing those defined in other layers.
     * </p>
     *
     * @param classNames the new style or styles of the component as a space-separated
     *                   list
     * @return this (for method chaining)
     * @see #getClassName()
     * @see #addClassName(String)
     * @see #removeClassName(String)
     */
    default S withClassName(String... classNames) {
        for (String className : classNames) {
            addClassName(className);
        }
        return (S) this;
    }
}
