package org.vaadin.firitin.util;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.dom.Element;
import org.vaadin.firitin.fluency.ui.FluentHasSize;
import org.vaadin.firitin.fluency.ui.FluentHasStyle;

/**
 * A utility class for creating SVG components in Vaadin.
 * This class extends the Component class and provides constructors
 * to create an SVG element with specified dimensions or default dimensions.
 */
public class VSvg extends Component implements FluentHasSize<VSvg>, FluentHasStyle<VSvg> {

    public VSvg(int minX, int minY, int width, int height) {
        super(new SvgElement(minX, minY, width, height));
    }

    public VSvg() {
        super(SvgElement.emptySvgRoot());
    }

    @Override
    public SvgElement getElement() {
        return (SvgElement) super.getElement();
    }
}
