package org.vaadin.firitin.components.button;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.server.Command;
import org.vaadin.firitin.components.progressbar.VProgressBar;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * A button that can be used to run a slow action in the background <strong>without blocking the UI</strong>.
 * Even if you would disable/block other parts of your UI during the action, this can be better approach as the global
 * progress indicator can make people think the app has crashed. A good UI pattern for long-running actions (that can't
 * run on background and keep the UI funtional), is to show for example a dialog with a progress indicator and possibly
 * a cancel button.
 * <p>
 * NOTE! This class is still in early development and likely to get some changes still in the future.
 * Suggestions/contributions are more than welcome!
 * <p>
 * The button is disabled while the task is running (which blocks re-triggering) and re-enabled when
 * it is done; {@code aria-busy} marks it busy for assistive technology. The busy state is shown as
 * a built-in (indeterminate) progress bar for buttons with text, and — since a greyed-out static
 * icon is poor feedback and there is no room for a bar — as a spinner replacing the icon for
 * icon-only buttons. A trackable/estimated action shows the (determinate) progress bar even on an
 * icon-only button. See {@link #isShowSpinner()} / {@link #setShowSpinner(boolean)} /
 * {@link #setBusyIcon(Component)} and {@link #isShowProgressBar()} / {@link #setShowProgressBar(boolean)}.
 * <p>
 * The actual task, set with {@link #setAction(Supplier)} or {@link #setCompletableFutureAction(Supplier)}, is run in a
 * separate thread. If your task wants to update the UI during its execution, you need to synchronize with the UI thread
 * using {@link UI#access(Command)}.
 * <p>
 * UI updates are suggested to be done in #setPostTaskAction(Consumer) which is called after the task has completed or
 * #setPreTaskAction(Runnable) which is called before the task is started. These are run in the UI thread.
 * <p>
 * The button will automatically enable polling if push is not enabled and call push() if "manual server push" is
 * active.
 *
 * @param <T> the type of the result of the slow task
 */
@StyleSheet("context://assets/org/vaadin/firitin/components/action-button.css")
public class ActionButton<T> extends Composite<Div> {

    private Integer estimatedDuration;
    private Supplier<T> action;
    private Consumer<? super T> postUiUpdate;
    private Runnable preUiUpdate;
    private UI ui;
    private CompletableFuture<T> completableFuture;
    private Boolean showProgressBar;
    private VProgressBar progressBar;
    private Supplier<CompletableFuture<T>> completableFutureSupplier;
    private Executor executor;
    private UIFuture uiFuture;
    private boolean enableAfterAction = true;
    private VButton button = new VButton();
    private String busyText;
    private String buttonText;

    private Boolean showSpinner;
    private Component busyIcon;
    private Component iconBeforeBusy;
    private boolean busyIconActive;

    public ActionButton() {
        super();
        getContent().getStyle().setDisplay(Style.Display.INLINE_BLOCK);
        getContent().getStyle().setPosition(Style.Position.RELATIVE);
        getContent().add(button);
        getButton().setDisableOnClick(true);
        getButton().addClickListener(this::handleClick);
    }

    public ActionButton(String buttonText, Supplier<CompletableFuture<T>> action) {
        this();
        setText(buttonText);
        setCompletableFutureAction(action);
    }

    public ActionButton(String buttonText, Runnable action) {
        this();
        setText(buttonText);
        setAction(() -> {
            action.run();
            return null;
        });
    }

    public ActionButton(String buttonText) {
        this();
        setText(buttonText);
    }

    public ActionButton<T> setAction(Supplier<T> action) {
        this.action = action;
        return this;
    }

    public ActionButton<Void> setAction(Runnable action) {
        this.action = () -> {
            action.run();
            return null;
        };
        return (ActionButton<Void>) this;
    }

    public ActionButton<T> setCompletableFutureAction(Supplier<CompletableFuture<T>> task) {
        this.completableFutureSupplier = task;
        return this;
    }

    /**
     * Sets an action to update the UI after the actual slow actions is completed. This is "run in the UI thread",
     * meaning you don't need to synchronize with the UI using UI.access().
     *
     * @param postUiAction the action to run after the slow task
     * @return this for chaining
     */
    public ActionButton<T> setPostUiAction(Consumer<? super T> postUiAction) {
        this.postUiUpdate = postUiAction;
        return this;
    }

    /**
     * @deprecated use {@link #setPostUiAction(Consumer)} instead
     * @param postUiUpdate the action to run after the slow task
     * @return this for chaining
     */
    @Deprecated
    public ActionButton<T> setPostUiUpdate(Consumer<? super T> postUiUpdate) {
        this.postUiUpdate = postUiUpdate;
        return this;
    }

    /**
     * Sets an action to update the UI before the actual slow actions is started. This is "run in the UI thread",
     * meaning you don't need to synchronize with the UI using UI.access().
     *
     * @param preUiAction the action to run before the slow task
     * @return this for chaining
     */
    public ActionButton<T> setPreUiAction(Runnable preUiAction) {
        this.preUiUpdate = preUiAction;
        return this;
    }

    /**
     * @deprecated use {@link #setPreUiAction(Runnable)} instead
     */
    @Deprecated(forRemoval = true)
    public ActionButton<T> setPreUiUpdate(Runnable preUiUpdate) {
        return setPreUiAction(preUiUpdate);
    }
    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        this.ui = attachEvent.getUI();
        this.uiFuture = new UIFuture(ui);
        if (executor != null) {
            uiFuture.setExecutor(executor);
        }
    }

    // TODO accessibility improvements to consider:
    //  1. Announce completion via an aria-live="polite" region (e.g. "Done" or
    //     the result), instead of relying on the app showing a Notification.
    //  2. Announce the busy state via a live region too ("Loading…"); some
    //     screen readers do not announce an aria-busy change on a disabled
    //     element.
    //  3. Give the progress bar an accessible name (setAriaLabel) while running,
    //     e.g. "Translating…", for the determinate case.
    //  4. For trackable icon-only buttons, a determinate "progress ring"
    //     (conic-gradient/SVG) would read better than the linear bar under a
    //     lone icon.

    private void handleClick() {
        // The button is disabled on click (setDisableOnClick), which prevents
        // re-triggering. For icon-only buttons we also swap the icon to a
        // spinner so the busy state reads as "working", not just "disabled".
        // aria-busy tells assistive technology the control is processing.
        getButton().getElement().setAttribute("aria-busy", "true");
        if (isShowSpinner()) {
            startSpinner();
        }

        if (preUiUpdate != null) {
            preUiUpdate.run();
        }
        if (isShowProgressBar()) {
            if (progressBar == null) {
                progressBar = prepareProgressBar();
            }
            if (estimatedDuration != null) {
                progressBar.setIndeterminate(false);
                progressBar.setValue(0);
                progressBar.setMax(estimatedDuration);
                progressBar.animateToEstimate();
            } else {
                progressBar.setIndeterminate(true);
            }
            progressBar.setVisible(true);
        }

        if(busyText != null) {
            buttonText = getButton().getText();
            getButton().setText(busyText);
        }

        if (completableFutureSupplier != null) {
            completableFuture = completableFutureSupplier.get();
        } else {
            if (executor != null) {
                completableFuture = CompletableFuture.supplyAsync(() -> action.get(), executor);
            } else {
                completableFuture = CompletableFuture.supplyAsync(() -> action.get());
            }
        }

        uiFuture.of(completableFuture).whenComplete((result, e) -> {
            reEnableAfterAction();
            if (postUiUpdate != null && e == null) {
                postUiUpdate.accept(result);
            }
            // TODO needs a separate error handling task!?
        });

    }

    public CompletableFuture<T> getCompletableFuture() {
        return completableFuture;
    }

    public void setText(String s) {
        getButton().setText(s);
    }

    // --- Convenience delegates to the underlying button, so common button
    // --- configuration can be done without going through getButton().

    /** Sets the button's icon. Delegates to the underlying {@link VButton}. */
    public ActionButton<T> setIcon(Component icon) {
        getButton().setIcon(icon);
        return this;
    }

    /** Adds theme variants to the underlying {@link VButton}. */
    public ActionButton<T> addThemeVariants(ButtonVariant... variants) {
        getButton().addThemeVariants(variants);
        return this;
    }

    /** Removes theme variants from the underlying {@link VButton}. */
    public ActionButton<T> removeThemeVariants(ButtonVariant... variants) {
        getButton().removeThemeVariants(variants);
        return this;
    }

    /** Sets the accessible name of the underlying {@link VButton}. */
    public ActionButton<T> setAriaLabel(String ariaLabel) {
        getButton().setAriaLabel(ariaLabel);
        return this;
    }

    /** Sets the tooltip of the underlying {@link VButton}. */
    public ActionButton<T> setTooltipText(String tooltipText) {
        getButton().setTooltipText(tooltipText);
        return this;
    }

    /**
     * Set the text of the button to show while the task is running (and button disabled).
     * @param text the text to show
     * @return
     */
    public ActionButton setBusyText(String text) {
        this.busyText = text;
        return this;
    }

    protected void reEnableAfterAction() {
        getButton().getElement().removeAttribute("aria-busy");
        if(isEnableAfterAction()) {
            getButton().setEnabled(true);
            if(busyText != null) {
                getButton().setText(buttonText);
            }
            stopSpinner();
        }
        if (progressBar != null) {
            progressBar.setVisible(false);
        }
    }

    private void startSpinner() {
        iconBeforeBusy = getButton().getIcon();
        getButton().setIcon(busyIcon != null ? busyIcon : createSpinner());
        busyIconActive = true;
    }

    private void stopSpinner() {
        if (busyIconActive) {
            getButton().setIcon(iconBeforeBusy);
            iconBeforeBusy = null;
            busyIconActive = false;
        }
    }

    /**
     * Creates the default busy indicator: an empty {@code vaadin-icon} drawn as
     * a spinning ring purely in CSS (see {@code action-button.css}). Being a
     * {@code vaadin-icon}, the theme sizes it exactly like the icon it replaces
     * (icon sizes vary a lot between themes), while the ring's thickness is
     * under our control rather than baked into a glyph. Override
     * {@link #setBusyIcon(Component)} for a custom one.
     */
    protected Component createSpinner() {
        Icon spinner = new Icon(); // empty vaadin-icon, drawn as a ring in CSS
        spinner.addClassName("action-button-spinner");
        // Decorative: the busy state is conveyed via aria-busy, and the button
        // keeps its own accessible name, so the spinner must not be announced.
        spinner.getElement().setAttribute("aria-hidden", "true");
        return spinner;
    }

    private boolean isIconOnly() {
        return getButton().getIcon() != null
                && (getButton().getText() == null || getButton().getText().isEmpty());
    }

    /**
     * Whether, while the task runs, the button's icon is replaced by a spinning
     * busy indicator (the button is also disabled, as always). By default this
     * is enabled automatically for <strong>icon-only</strong> buttons (an icon,
     * no text), where a greyed-out static icon is poor feedback and there is no
     * room for a progress bar. Buttons with text (or that otherwise show a
     * {@link #isShowProgressBar() progress bar}) use that bar instead. Set it
     * explicitly with {@link #setShowSpinner(boolean)} to force it on or off.
     */
    public boolean isShowSpinner() {
        if (showSpinner != null) {
            return showSpinner;
        }
        return isIconOnly() && !isShowProgressBar();
    }

    /**
     * Forces the spinning busy indicator on or off (see {@link #isShowSpinner()}
     * for the default behavior). Call before the button is attached.
     */
    public ActionButton<T> setShowSpinner(boolean showSpinner) {
        this.showSpinner = showSpinner;
        return this;
    }

    /**
     * Sets a custom component to show in place of the icon while the task runs
     * (instead of the default spinner). Implies {@link #isShowSpinner()}.
     */
    public ActionButton<T> setBusyIcon(Component busyIcon) {
        this.busyIcon = busyIcon;
        if (busyIcon != null && showSpinner == null) {
            showSpinner = true;
        }
        return this;
    }

    public boolean isShowProgressBar() {
        if (showProgressBar != null) {
            return showProgressBar;
        }
        // Buttons with text (or no icon) have room for the (by default
        // indeterminate) bar, which reads better than swapping the icon.
        // Icon-only buttons default to the spinner instead — a tiny bar under a
        // lone icon looks odd. (Enabling it explicitly, e.g. via an estimated
        // duration, takes precedence and shows the bar even for icon-only.)
        return !isIconOnly();
    }

    /**
     * @param showProgressBar true if the built-in progress bar should be shown while the task is running
     */
    public void setShowProgressBar(boolean showProgressBar) {
        this.showProgressBar = showProgressBar;
    }

    protected VProgressBar prepareProgressBar() {
        var progressBar = new VProgressBar();
        if (true) {
            // Absolute positioning right below the button
            progressBar.getStyle().setPosition(Style.Position.ABSOLUTE);
            progressBar.getStyle().setRight("0");
            progressBar.getStyle().setLeft("0");
            progressBar.getStyle().setBottom("0");
            progressBar.getStyle().setMargin("0");
            progressBar.getStyle().setDisplay(Style.Display.BLOCK);
        } else {
            // Inline block positioning right after the button text
            // TODO consider exposing this setup with API
            progressBar.getStyle().setDisplay(Style.Display.INLINE_BLOCK);
            progressBar.getStyle().setAlignItems(Style.AlignItems.BASELINE);
            progressBar.getStyle().setTextAlign(Style.TextAlign.CENTER);
            progressBar.getStyle().setMarginBottom("0");
            progressBar.getStyle().setMarginTop("0");
            progressBar.getStyle().setMarginLeft("1em");
            progressBar.getStyle().setHeight("0.5em");
            progressBar.setWidth("2em");
        }
        progressBar.setVisible(false);

        getContent().add(progressBar);
        return progressBar;
    }

    /**
     * API for updating the progress bar from the task. This method is safe to call from the task thread.
     *
     * @param progress the progress value to set
     * @param min      the minimum value of the progress bar
     * @param max      the maximum value of the progress bar
     */
    public void updateProgressAsync(double progress, double min, double max) {
        if (progressBar != null) {
            Command command = () -> {
                progressBar.setIndeterminate(false);
                progressBar.setMin(min);
                progressBar.setMax(max);
                if(progress > min) {
                    if (progress > max) {
                        progressBar.setIndeterminate(true);
                    } else {
                        progressBar.setValue(progress);
                    }
                }
            };
            CompletableFuture.runAsync(() -> {
                if(ui != null) {
                    ui.access(command);
                } else {
                    // If not attached, safe o update directly
                    command.execute();
                }
            });
        }
    }

    /**
     * API for updating the progress bar from the task. This method is safe to call from the task thread.
     *
     * @param progress the progress value to set
     */
    public void updateProgressAsync(double progress) {
        if (progressBar != null) {
            Command command = () -> {
                progressBar.setIndeterminate(false);
                if(progress > progressBar.getMax()) {
                    progressBar.setIndeterminate(true);
                } else {
                    progressBar.setValue(progress);
                }
            };
            CompletableFuture.runAsync(() -> {
                if(ui != null) {
                    ui.access(command);
                } else {
                    // If not attached, safe o update directly
                    command.execute();
                }
            });
        }
    }

    public Executor getExecutor() {
        return executor;
    }

    /**
     * Set an executor to be used for running the task. If not set, the default executor is used.
     *
     * @param executor the executor to use for the task
     */
    public void setExecutor(Executor executor) {
        this.executor = executor;
        if (uiFuture != null) {
            uiFuture.setExecutor(executor);
        }
    }

    public Integer getEstimatedDuration() {
        return estimatedDuration;
    }

    /**
     * Set the estimated duration of the task in milliseconds. This can be used to show a progress bar that is not
     * indeterminate.
     *
     * @param estimatedDuration
     */
    public void setEstimatedDuration(Integer estimatedDuration) {
        this.estimatedDuration = estimatedDuration;
        if(estimatedDuration != null) {
            setShowProgressBar(true);
        }
    }

    /**
     * Returns the underlying button component, for further configaration.
     *
     * @return the underlying button component
     */
    public VButton getButton() {
        return button;
    }

    /**
     * @return true if the button should be automatically enabled after the action is completed
     */
    public boolean isEnableAfterAction() {
        return enableAfterAction;
    }

    /**
     * @param enableAfterAction true (default) if the button should be automatically enabled after the action is
     *                          completed
     * @return this for chaining
     */
    public ActionButton setEnableAfterAction(boolean enableAfterAction) {
        this.enableAfterAction = enableAfterAction;
        return this;
    }
}
