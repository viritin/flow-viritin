package org.vaadin.firitin.components.button;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
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
 * NOTE! This class is still in early development and might change a lot stil in the future. Suggestions/contributions are
 * more than welcome.
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

    private Supplier<T> action;
    private Consumer<? super T> postUiUpdate;
    private Runnable preUiUpdate;
    private UI ui;
    private CompletableFuture<T> completableFuture;
    private VProgressBar progressBar;
    private Supplier<CompletableFuture<T>> completableFutureSupplier;
    private Executor executor;
    private UIFuture uiFuture;

    public ActionButton() {
        super();
        getContent().setDisableOnClick(true);
        getContent().addClickListener(this::handleClick);
    }

    public ActionButton(String buttonText) {
        this();
        setText(buttonText);
    }

    public void setAction(Supplier<T> action) {
        this.action = action;
    }

    public void setCompletableFutureAction(Supplier<CompletableFuture<T>> task) {
        this.completableFutureSupplier = task;
    }

    public void setPostUiUpdate(Consumer<? super T> postUiUpdate) {
        this.postUiUpdate = postUiUpdate;
    }

    public void setPreUiUpdate(Runnable preUiUpdate) {
        this.preUiUpdate = preUiUpdate;
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        this.ui = attachEvent.getUI();
        this.uiFuture = new UIFuture(ui);
        if(executor != null) {
            uiFuture.setExecutor(executor);
        }
    }

    private void handleClick() {

        if (preUiUpdate != null) {
            preUiUpdate.run();
        }
        if (progressBar != null) {
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

    public void showProgressBar(boolean show) {
        if (show == (progressBar != null)) {
            return;
        }
        if (show) {
            progressBar = new VProgressBar();
            progressBar.setIndeterminate(true);
            progressBar.setVisible(true);
            // TODO This is a hack and visually broken, figure out a better way to do this
            getContent().getElement().appendChild(progressBar.getElement());
        } else {
            if (progressBar != null) {
                progressBar.removeFromParent();
                progressBar = null;
            }
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
        if(uiFuture != null) {
            uiFuture.setExecutor(executor);
        }
    }
}
