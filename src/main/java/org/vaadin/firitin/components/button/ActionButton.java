package org.vaadin.firitin.components.button;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
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
 * The button will be disabled while the task is running and re-enabled when the task is done.
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
public class ActionButton<T> extends Composite<VButton> {

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

    public ActionButton() {
        super();
        getContent().setDisableOnClick(true);
        getContent().addClickListener(this::handleClick);
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
        this.postUiUpdate = postUiUpdate;
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
        this.preUiUpdate = preUiUpdate;
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

    private void handleClick() {

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
            reEnableAfterTask();
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
        getContent().setText(s);
    }

    protected void reEnableAfterTask() {
        getContent().setEnabled(true);
        if (progressBar != null) {
            progressBar.setVisible(false);
        }
    }

    public boolean isShowProgressBar() {
        return showProgressBar == null ? true : showProgressBar;
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
        getContent().getElement().appendChild(progressBar.getElement());
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
    public Button getButton() {
        return getContent();
    }

    /**
     * @return the underlying content component
     * @deprecated use {@link #getButton()} instead, the composition root is likely to change for improved A11y support
     */
    @Deprecated
    @Override
    public VButton getContent() {
        return super.getContent();
    }
}
