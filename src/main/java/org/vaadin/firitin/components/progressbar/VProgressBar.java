package org.vaadin.firitin.components.progressbar;

import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.progressbar.ProgressBarVariant;
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


    public VProgressBar withThemeVariants(ProgressBarVariant... variants) {
        addThemeVariants(variants);
        return this;
    }

    /**
     * Creates a progressbar that is visible in the UI until the given the
     * long(ish) running task has been executed.
     * The progressbar will be added (and rendered) to the UI and then the
     * task will be executed in the UI thread. Once the task is done, the
     * progress indicator will automatically be removed. Note that the UI
     * will be blocked during the execution, so for really long tasks
     * where you expect users to be able to continue working with other
     * features in the UI, this helper is not the way to go.
     *
     * @param task the task to be finished before the returned progressbar
     *             will be vanished
     * @return the progressbar to be added to UI
     */
    public static VProgressBar indeterminateForTask(Runnable task) {
        VProgressBar progressBar = new VProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.addAttachListener(attachEvent -> {
            attachEvent.getUI().getPage().executeJs("").then(v -> {
                task.run();
                progressBar.removeFromParent();
            });
        });
        return progressBar;
    }

}
