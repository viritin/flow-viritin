package org.vaadin.firitin;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.router.Route;
import org.jetbrains.annotations.NotNull;
import org.vaadin.firitin.components.button.ActionButton;
import org.vaadin.firitin.components.button.UIFuture;
import org.vaadin.firitin.components.checkbox.VCheckBox;
import org.vaadin.firitin.components.notification.VNotification;
import org.vaadin.firitin.components.progressbar.VProgressBar;
import org.vaadin.firitin.layouts.HorizontalFloatLayout;
import reactor.core.publisher.Mono;

import java.time.LocalTime;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Route
public class SlowTaskView extends VerticalLayout {

    private CompletableFuture<Void> future;

    public SlowTaskView() {

        Button button = new Button("Test UI button");
        button.setTooltipText("Click to test if the UI is blocked or not");
        button.addClickListener(event -> {
            Notification.show("I'm alive!");
        });
        add(button);

        add(new H2("ActionButton usage"));

        var showDialog = new VCheckBox("Show dialog during task");
        var showNotificationOnStart = new VCheckBox("Show notification on start");
        var disableUI = new VCheckBox("Disable UI during task");
        var builtInProgressbar = new VCheckBox("Built in progress bar").withValue(true)
                .withTooltip("On by default, implicitly off if preUiAction defined (like dialog in this example). Toggle this to enable explicitly.");
        var estimate = new VCheckBox("Define estimated time to 4 secs (really 5)");
        var busyText = new VCheckBox("Custom busy text");
        add(new HorizontalFloatLayout(showDialog, showNotificationOnStart, disableUI, builtInProgressbar, estimate, busyText));

        Dialog taskInProgressDialog = new Dialog();
        taskInProgressDialog.setHeaderTitle("Computing things, please wait...");
        taskInProgressDialog.add(new Paragraph("This dialog is optional and built in preTaskAction. One could e.g. add a cancel button or show progress/messages from the task"));

        VProgressBar progressBarInDialog = new VProgressBar();
        progressBarInDialog.setIndeterminate(true);
        taskInProgressDialog.add(progressBarInDialog);
        taskInProgressDialog.add(new Button("Test UI button", event -> {
            Notification.show("I'm alive!");
        }));
        taskInProgressDialog.setModal(true);

        // The actual component usage
        ActionButton actionButton = new ActionButton("Compute things...");

        Supplier<String> basicSlowAction = () -> {
            // This is the actual task, executed later in a separate thread
            // DO NOT MODIFY UI HERE!
            return slowGetString();
        };
        Supplier<String> trackableSlowAction = () -> {
            // This is the actual task, executed later in a separate thread
            // DO NOT MODIFY UI HERE!
            return slowGetStringWithNotifier(progress -> {
                // The action button provides an API to update the progress bar, synced automatically, no need for UI.access
                actionButton.updateProgressAsync(progress, 0, 1);
            });
        };
        Select<Supplier<String>> supplierSelect = new Select<>();
        supplierSelect.setLabel("Select action:");
        supplierSelect.setItems(basicSlowAction, trackableSlowAction);
        supplierSelect.setItemLabelGenerator(s -> s == basicSlowAction ? "Basic slow action" : "Trackable slow action");
        supplierSelect.addValueChangeListener(event -> {
            actionButton.setAction(event.getValue());
        });
        supplierSelect.setValue(basicSlowAction);
        add(supplierSelect);

        // Optionally you can provide an Executor that will be used to run the task
        // nonBlockingTaskButton.setExecutor(Executors.newSingleThreadExecutor());

        // The task can also return a CompletableFuture
        //nonBlockingTaskButton.setCompletableFutureAction(() -> computeSlowString());

        builtInProgressbar.addValueChangeListener(event -> {
            actionButton.setShowProgressBar(event.getValue());
        });

        actionButton.setPreUiAction(() -> {
            if (busyText.getValue()) {
                actionButton.setBusyText("Please wait for heavy things...");
            }
            // In this task one can modify UI, this task is optional
            if (showNotificationOnStart.getValue()) {
                Notification.show("Starting the task...");
            }
            if (showDialog.getValue()) {
                taskInProgressDialog.open();
            }
            if (disableUI.getValue()) {
                UI.getCurrent().setEnabled(false);
            }
            if (estimate.getValue()) {
                if (builtInProgressbar.getValue()) {
                    actionButton.setEstimatedDuration(4000);
                }
                progressBarInDialog.setMax(4000);
                progressBarInDialog.animateToEstimate();
                progressBarInDialog.setIndeterminate(false);
            }
            if (builtInProgressbar.getValue()) {
                actionButton.setShowProgressBar(builtInProgressbar.getValue());
            }
        });
        actionButton.setPostUiUpdate(s -> {
            // In this task one can modify UI
            VNotification.prominent("Slow string:" + s);
            taskInProgressDialog.close();
            if (disableUI.getValue()) {
                UI.getCurrent().setEnabled(true);
            }

        });

        add(actionButton);

        taskInProgressDialog.add(new Button("Cancel", event -> {
            actionButton.getCompletableFuture().cancel(true);
            taskInProgressDialog.close();
        }));


        // Check if task running button
        Button checkTaskButton = new Button("Check if task is running");
        checkTaskButton.addClickListener(event -> {
            if (actionButton.getCompletableFuture() == null) {
                Notification.show("Task not started yet!");
            } else if (actionButton.getCompletableFuture().isDone()) {
                Notification.show("Task is done!");
            } else {
                Notification.show("Task is still running...");
            }
        });

        add(checkTaskButton);

        add(new H2("Lower level UIFuture usage"));

        UIFuture uiFuture = new UIFuture();
        add(new HorizontalFloatLayout(
                new Button("Test with CompletableFuture", event -> {
                    Notification.show("Starting a task that will take 5 seconds");
                    future = uiFuture.of(computeSlowString())
                            .thenApply(result -> {
                                Notification.show("Result: " + result);
                                return result.length();
                            })
                            .thenAccept(length -> Notification.show("Length of the result: " + length));
                }),
                new Button("supplyAsync", event -> {
                    Notification.show("Starting a task that will take 5 seconds");
                    future = uiFuture.supplyAsync(() -> slowGetString())
                            .thenApply(result -> {
                                Notification.show("Result: " + result);
                                return result.length();
                            })
                            .thenAccept(length -> Notification.show("Length of the result: " + length));
                }),
                new Button("runAsync", event -> {
                    Notification.show("Starting a task that will take 2 seconds");
                    future = uiFuture.runAsync(() -> {
                                try {
                                    Thread.sleep(2000);
                                } catch (InterruptedException e) {
                                    throw new RuntimeException(e);
                                }
                                System.out.println("Done!");
                            })
                            .thenRun(() -> Notification.show("Something happened for sure, but the UI did not care!"));
                })));

        add(new Button("Cancel action", event -> {
            if (future != null) {
                if (future.isDone()) {
                    Notification.show("Action already finished!");
                } else {
                    future.cancel(true);
                    Notification.show("Cancelled action");
                }
            } else {
                Notification.show("No action to cancel!");
            }
        }));

    }

    private static @NotNull String slowGetString() {
        LocalTime start = LocalTime.now();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "Done! " + start + " -> " + LocalTime.now();
    }

    private static @NotNull String slowGetStringWithNotifier(Consumer<Double> progressListener) {
        progressListener.accept(0.0);
        LocalTime start = LocalTime.now();
        for (int i = 0; i < 20; i++) {
            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            progressListener.accept(i / 20.0);
        }
        return "Done! " + start + " -> " + LocalTime.now();
    }

    public Mono<String> monoString() {
        return Mono.fromFuture(computeSlowString());
    }

    public CompletableFuture<String> computeSlowString() {
        return CompletableFuture.supplyAsync(() -> {
            return slowGetString();
        });
    }

}
