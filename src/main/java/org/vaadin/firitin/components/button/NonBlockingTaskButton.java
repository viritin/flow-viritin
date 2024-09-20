package org.vaadin.firitin.components.button;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.PushConfiguration;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.Command;
import com.vaadin.flow.shared.communication.PushMode;
import org.vaadin.firitin.components.progressbar.VProgressBar;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Logger;

/**
 * A button that can be used to run a slow task in the background <strong>without blocking the UI</strong>.
 * Even if you would disable/block other parts of your UI during the task, this can be better approach as the global
 * progress indicator can make people think the app has crashed. A good UI pattern for long running tasks (that can't
 * run on background), is to show for example a dialog with a progress indicator and possibly a cancel button.
 *
 * <p>
 * This class is still in early development and might change a lot stil in the future. Suggestions/contributions are
 * more than welcome.
 * <p>
 * The button will be disabled while the task is running and re-enabled when the task is done.
 * <p>
 * The actual task, set with {@link #setTask(Supplier)} or {@link #setCompletableFutureTask(Supplier)}, is run in a
 * separate thread. If your task wants to update the UI during its execution, you need to synchronize with the UI thread
 * using {@link UI#access(Command)}.
 * <p>
 * UI updates are suggested to be done in #setPostTaskAction(Consumer) which is called after the task has completed or
 * #setPreTaskAction(Runnable) which is called before the task is started. These are run in the UI thread.
 * <p>
 * The button will automatically enable polling if push is not enabled.
 *
 * @param <T> the type of the result of the slow task
 */
public class NonBlockingTaskButton<T> extends Composite<VButton> {

    private boolean pollingEnabled;
    private Supplier<T> task;
    private Consumer<? super T> postTaskAction;
    private Runnable preTaskAction;
    private UI ui;
    private CompletableFuture<T> completableFuture;
    private VProgressBar progressBar;
    private Supplier<CompletableFuture<T>> cfTask;
    private Executor executor;

    public NonBlockingTaskButton() {
        super();
        getContent().setDisableOnClick(true);
        getContent().addClickListener(this::handleClick);
    }

    public NonBlockingTaskButton(String buttonText) {
        this();
        setText(buttonText);
    }

    public void setTask(Supplier<T> task) {
        this.task = task;
    }

    public void setCompletableFutureTask(Supplier<CompletableFuture<T>> task) {
        this.cfTask = task;
    }

    public void setPostTaskAction(Consumer<? super T> postTaskAction) {
        this.postTaskAction = postTaskAction;
    }

    public void setPreTaskAction(Runnable preTaskAction) {
        this.preTaskAction = preTaskAction;
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        this.ui = attachEvent.getUI();
    }

    private void handleClick() {
        enablePollingIfPushNotConfigured();

        if (preTaskAction != null) {
            preTaskAction.run();
        }
        if (progressBar != null) {
            progressBar.setVisible(true);
        }
        if (cfTask != null) {
            completableFuture = cfTask.get();
        } else {
            if (executor != null) {
                completableFuture = CompletableFuture.supplyAsync(() -> task.get(), executor);
            } else {
                completableFuture = CompletableFuture.supplyAsync(() -> task.get());
            }
        }
        completableFuture.handle((result, e) -> {
            ui.access(() -> {
                reEnableAfterTask();
                if (postTaskAction != null && e == null) {
                    postTaskAction.accept(result);
                }
                // TODO needs a separate error handling task??
            });
            return result;
        });
    }

    public CompletableFuture<T> getCompletableFuture() {
        return completableFuture;
    }

    public void setText(String s) {
        getContent().setText(s);
    }

    protected void enablePollingIfPushNotConfigured() {
        PushConfiguration pushConfiguration = ui.getPushConfiguration();
        PushMode pushMode = pushConfiguration.getPushMode();
        if (pushMode == PushMode.DISABLED && ui.getPollInterval() == -1) {
            // no push enabled, lets enable polling
            ui.setPollInterval(1000);
            pollingEnabled = true;
            // log a message that polling is enabled
            Logger.getLogger(NonBlockingTaskButton2.class.getName()).fine("Polling enabled for SlowTaskButton. Consider enabling push instead.");
        }
    }

    protected void reEnableAfterTask() {
        getContent().setEnabled(true);
        if (pollingEnabled) {
            UI.getCurrent().setPollInterval(-1);
            pollingEnabled = false;
        }
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
    }
}
