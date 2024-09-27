package org.vaadin.firitin.components.progressbar;

import com.vaadin.flow.component.page.Page;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.progressbar.ProgressBarVariant;
import org.vaadin.firitin.components.button.UIFuture;
import org.vaadin.firitin.fluency.ui.FluentComponent;
import org.vaadin.firitin.fluency.ui.FluentHasSize;
import org.vaadin.firitin.fluency.ui.FluentHasStyle;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class VProgressBar extends ProgressBar implements FluentComponent<VProgressBar>, FluentHasSize<VProgressBar>, FluentHasStyle<VProgressBar> {

    private boolean prepareForOverdueInAnimation = true;

    public VProgressBar() {
        super();
    }

    public VProgressBar(double min, double max) {
        super(min, max);
    }

    public VProgressBar(double min, double max, double value) {
        super(min, max, value);
    }

    /**
     * Creates a progressbar that is visible in the UI until the given the long(ish) running task has been executed.
     * The progressbar will be added (and rendered) to the UI and then the task will be executed in the UI thread. Once
     * the task is done, the progress indicator will automatically be removed. The progress indicator provided by the
     * framework is hidden during the taask. Note that the UI will be blocked during the execution, so for really long
     * tasks where you expect users to be able to continue working with other features in the UI, this helper is not the
     * way to go. See {@link org.vaadin.firitin.components.button.UIFuture}.
     *
     * @param task the task to be finished before the returned progressbar will be vanished
     * @return the progressbar to be added to UI
     */
    public static VProgressBar indeterminateForTask(Runnable task) {
        VProgressBar progressBar = new VProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.addAttachListener(attachEvent -> {
            Page page = attachEvent.getUI().getPage();
            page.executeJs("document.querySelector('vaadin-connection-indicator').style.display = 'none';")
                    .then(v -> {
                        task.run();
                        progressBar.removeFromParent();
                        page.executeJs("document.querySelector('vaadin-connection-indicator').style.display = '';");
                    });
        });
        return progressBar;
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
     * Runs a client side animation between the given {@link LocalDateTime}s. The animation starts from now
     * and ends at the given estimated end time. The progressbar will be set to indeterminate mode if the estimate is
     * passed.
     *
     * <p>
     * You can use #finish() to stop the animation and set the progress to 100%.
     *
     * @param startOfProgress the time when the progress has started
     * @param estimatedEndOfProgress the time when the progress is estimated to end
     */
    public void animateToEstimate(LocalDateTime startOfProgress, LocalDateTime estimatedEndOfProgress) {
        double max = startOfProgress.until(estimatedEndOfProgress, ChronoUnit.MILLIS);
        double now = startOfProgress.until(LocalDateTime.now(), ChronoUnit.MILLIS);
        setMin(0);
        setMax(max);
        if(now > max) {
            setValue(max);
        } else {
            setValue(now);
        }
        animateToEstimate();
    }

    /**
     * Runs a client side animation to the {@link #getMax()} value as "milliseconds". If you for example expect your
     * progress to take around 5000ms, use 5000 as max value. The progress animation will slow down in the end of the
     * progressbar, so it doesn't matter if your estimate is slightly optimistic. If the estimate is passed a lot, the
     * progressbar will switch to indeterminate mode.
     */
    public void animateToEstimate() {
        getElement().executeJs("""
                  const progressBar = this;
                  const estimatedDuration = $0;
                  const beginning = progressBar.value;
                  const start = new Date().getTime();
                  const prepareForOverdueInAnimation = $1;
                  const step = () => {
                    if(progressBar.finished) {
                      console.log("Stopped animation as already finished");
                      return;
                    }
                    var elapsed = beginning + new Date().getTime() - start;
                    if(prepareForOverdueInAnimation) {
                        // slow down the animation when it's almost done to play some time if estimate is passed
                        if(elapsed > estimatedDuration * 0.8) {
                          // TODO make this somehow smoother
                          elapsed = estimatedDuration * 0.8 + (elapsed - estimatedDuration * 0.8) * 0.2;
                        }
                    }
                    
                    if(elapsed >= estimatedDuration) {
                        // if still over estimate, stop animation and switch to indeterminate mode
                        progressBar.indeterminate = true;
                    } else {
                        progressBar.value = elapsed;
                    }
                    if(document.body.contains(progressBar) && !progressBar.hidden) {
                        requestAnimationFrame(step);
                    }
                  }
                  requestAnimationFrame(step);
                """, getMax(), prepareForOverdueInAnimation);
    }

    public void finish() {
        getElement().executeJs("this.finished = true; this.indeterminate = false;");
        setValue(getMax());
    }

    public boolean isPrepareForOverdueInAnimation() {
        return prepareForOverdueInAnimation;
    }

    public VProgressBar setPrepareForOverdueInAnimation(boolean prepareForOverdueInAnimation) {
        this.prepareForOverdueInAnimation = prepareForOverdueInAnimation;
        return this;
    }

}
