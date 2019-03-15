package org.vaadin.firitin.components.progressbar;

import com.vaadin.flow.component.progressbar.ProgressBar;
import org.vaadin.firitin.fluency.ui.FluentComponent;
import org.vaadin.firitin.fluency.ui.FluentHasSize;
import org.vaadin.firitin.fluency.ui.FluentHasStyle;

public class VProgressBar extends ProgressBar implements FluentComponent<VProgressBar>, FluentHasSize<VProgressBar>, FluentHasStyle<VProgressBar> {

    public VProgressBar() {
        super();
    }

    public VProgressBar(double min, double max) {
        super(min, max);
    }

    public VProgressBar(double min, double max, double value) {
        super(min, max, value);
    }

    public VProgressBar withValue(double value) {
        setValue(value);
        return this;
    }

    public VProgressBar withMax(double max) {
        setMax(max);
        return this;
    }

    public VProgressBar withMin(double min) {
        setMin(min);
        return this;
    }
    
}
