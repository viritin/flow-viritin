package org.vaadin.firitin;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.jetbrains.annotations.NotNull;
import org.vaadin.firitin.components.button.NonBlockingTaskButton;
import org.vaadin.firitin.components.checkbox.VCheckBox;
import org.vaadin.firitin.components.progressbar.VProgressBar;
import org.vaadin.firitin.layouts.HorizontalFloatLayout;
import reactor.core.publisher.Mono;

import java.time.LocalTime;
import java.util.concurrent.CompletableFuture;

@Route
public class SlowTaskView extends VerticalLayout {

    public SlowTaskView() {

        Button button = new Button("Test UI button");
        button.setTooltipText("Click to test if the UI is blocked or not");
        button.addClickListener(event -> {
            Notification.show("I'm alive!");
        });
        add(button);

        var showDialog = new VCheckBox("Show dialog during task");
        var showNotificationOnStart = new VCheckBox("Show notification on start");
        var disableUI = new VCheckBox("Disable UI during task");
        var builtInProgressbar = new VCheckBox("Built in progress bar");
        add(new HorizontalFloatLayout(showDialog, showNotificationOnStart, disableUI, builtInProgressbar));

        Dialog taskInProgressDialog = new Dialog();
        taskInProgressDialog.setHeaderTitle("Computing things, please wait...");
        taskInProgressDialog.add(new Paragraph("This dialog is optional and built in preTaskAction. One could e.g. add a cancel button or show progress/messages from the task"));

        VProgressBar progressBar = new VProgressBar();
        progressBar.setIndeterminate(true);
        taskInProgressDialog.add(progressBar);
        taskInProgressDialog.add(new Button("Test UI button", event -> {
            Notification.show("I'm alive!");
        }));
        taskInProgressDialog.setModal(true);

        // The actual component usage
        NonBlockingTaskButton nonBlockingTaskButton = new NonBlockingTaskButton("Compute things...");
        nonBlockingTaskButton.setTask(() -> {
            // This is the actual task, executed later in a separate thread
            // DO NOT MODIFY UI HERE!
            return slowGetString();
        });
        // Optionally you can provide an Executor that will be used to run the task
        // nonBlockingTaskButton.setExecutor(Executors.newSingleThreadExecutor());

        // The task can also return a CompletableFuture
        //nonBlockingTaskButton.setCompletableFutureTask(() -> computeSlowString());

        nonBlockingTaskButton.setPreTaskAction(() -> {
            // In this task one can modify UI, this task is optional
            if(showNotificationOnStart.getValue()) {
                Notification.show("Starting the task...");
            }
            if(showDialog.getValue()) {
                taskInProgressDialog.open();
            }
            if(disableUI.getValue()) {
                UI.getCurrent().setEnabled(false);
            }
            nonBlockingTaskButton.showProgressBar(builtInProgressbar.getValue());
        });
        nonBlockingTaskButton.setPostTaskAction(s -> {
            // In this task one can modify UI
            add(new Paragraph("Slow string: " + s));
            taskInProgressDialog.close();
            if(disableUI.getValue()) {
                UI.getCurrent().setEnabled(true);
            }

        });

        add(nonBlockingTaskButton);

        taskInProgressDialog.add(new Button("Cancel", event -> {
            nonBlockingTaskButton.getCompletableFuture().cancel(true);
            taskInProgressDialog.close();
        }));


        // Check if task running button
        Button checkTaskButton = new Button("Check if task is running");
        checkTaskButton.addClickListener(event -> {
            if(nonBlockingTaskButton.getCompletableFuture() == null) {
                Notification.show("Task not started yet!");
            } else  if (nonBlockingTaskButton.getCompletableFuture().isDone()) {
                Notification.show("Task is done!");
            } else {
                Notification.show("Task is still running...");
            }
        });

        add(checkTaskButton);

    }

    public Mono<String> monoString() {
        return Mono.fromFuture(computeSlowString());
    }

    public CompletableFuture<String> computeSlowString() {
        return CompletableFuture.supplyAsync(() -> {
            return slowGetString();
        });
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

}
