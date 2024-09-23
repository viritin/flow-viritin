package org.vaadin.firitin.components.button;

import com.vaadin.flow.component.PushConfiguration;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.shared.communication.PushMode;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.logging.Logger;

/**
 * This helper class is used to execute long-running actions and provide an easy way to run updates once the action is
 * finished.
 * <p>
 *     The helper takes care of enabling polling if push is not enabled and synchronizes the UI updates with the UI thread.
 *     If the manual push mode is used, the server push is triggered automatically after UI updates are done.
 */
public class UIFuture {

    private int actions = 0;

    private final UI ui;
    private PushMode pushMode;
    private boolean pollingEnabled;

    public UIFuture(UI ui) {
        this.ui = ui;
    }

    public UIFuture() {
        // If UI is not provided, expect that the current UI is used
        this(UI.getCurrent());
    }

    private boolean asyncUiUpdate = false;

    private Executor executor;

    public boolean isAsyncUiUpdate() {
        return asyncUiUpdate;
    }

    public void setAsyncUiUpdate(boolean asyncUiUpdate) {
        this.asyncUiUpdate = asyncUiUpdate;
    }

    public Executor getExecutor() {
        return executor;
    }

    private void ensurePushOrPolling() {
        actions++;
        if(actions == 1) {
            PushConfiguration pushConfiguration = ui.getPushConfiguration();
            pushMode = pushConfiguration.getPushMode();
            if (pushMode == PushMode.DISABLED && ui.getPollInterval() == -1) {
                // no push enabled, lets enable polling
                ui.setPollInterval(1000);
                pollingEnabled = true;
                // log a message that polling is enabled
                Logger.getLogger(NonBlockingTaskButton2.class.getName()).fine("Polling enabled for SlowTaskButton. Consider enabling push instead.");
            }
        }
    }

    private void shutDownPolling() {
        actions--;
        if (pollingEnabled && actions == 0) {
            UI.getCurrent().setPollInterval(-1);
            pollingEnabled = false;
        }
    }

    /**
     * Sets the executor that will be used to run the task. A default by JDK is used if not set.
     *
     * @param executor
     */
    public void setExecutor(Executor executor) {
        this.executor = executor;
    }

    /**
     * Returns a completable future whose additional actions are executed and properly synchronized in the UI thread.
     * Note, that if you use the async methods of the completable future, you need to synchronize with the UI thread
     * yourself.
     *
     * @param task
     * @param <T>
     * @return
     */
    public <T> CompletableFuture<T> of(final CompletableFuture<T> task) {
        ensurePushOrPolling();
        CompletableFuture<T> future = new CompletableFuture<>();
        BiConsumer<T, Throwable> handler = (result, throwable) -> {
            ui.access(() -> {
                if (throwable != null) {
                    future.completeExceptionally(throwable);
                } else {
                    future.complete(result);
                }
                shutDownPolling();
                if(pushMode == PushMode.MANUAL) {
                    // Don't really know when or who should use this mode, but it's there...
                    ui.push();
                }
            });
        };

        if(asyncUiUpdate) {
            if(executor != null) {
                task.whenCompleteAsync(handler, executor);
            } else {
                task.whenCompleteAsync(handler);
            }
        } else  {
            task.whenComplete(handler);
        }
        return future;
    }

    public <T> CompletableFuture<T> supplyAsync(Supplier<T> supplier) {
        CompletableFuture<T> future;
        if(executor != null) {
            future = CompletableFuture.supplyAsync(supplier, executor);
        } else {
            future = CompletableFuture.supplyAsync(supplier);
        }
        return of(future);
    }

    public CompletableFuture<Void> runAsync(Runnable runnable) {
        CompletableFuture<Void> future;
        if(executor != null) {
            future = CompletableFuture.runAsync(runnable, executor);
        } else {
            future = CompletableFuture.runAsync(runnable);
        }
        return of(future);
    }
}
