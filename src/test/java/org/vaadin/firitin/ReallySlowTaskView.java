package org.vaadin.firitin;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.router.Route;
import org.vaadin.firitin.components.RichText;
import org.vaadin.firitin.components.button.UIFuture;
import org.vaadin.firitin.components.grid.VGrid;
import org.vaadin.firitin.components.progressbar.VProgressBar;
import org.vaadin.firitin.layouts.HorizontalFloatLayout;
import org.vaadin.firitin.testdomain.AppWideTasks;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.CompletableFuture;

@Route
public class ReallySlowTaskView extends VerticalLayout {

    private final UIFuture uiFuture;
    Map<AppWideTasks.Task, VProgressBar> taskToProgressBar = new WeakHashMap<>();
    private TaskGrid grid;
    private CompletableFuture<Void> future;
    private Map<AppWideTasks.Task, CompletableFuture<String>> taskToSubscribtion = new HashMap<>();

    public ReallySlowTaskView(AppWideTasks appWideTasks) {
        add(new H1("Very long tasks"));
        add(new RichText().withMarkDown("""
                What it your tasks are system wide and you session might close between? This example simulates
                that kind of scenario. You'll essentially need to have some sort of API to know currently running
                tasks and to subscribe to their results (and/or progress). The service in this example allows to 
                subscribe for the result with CompletableFuture, so hooking to some UI action once the result is 
                available is easy. The demo hooks to currently running system wide tasks when arriving to the view
                and registers to get notified when the task is done. By refreshing, you'll also see others's new
                tasks and can subscribe to those as well. The service keeps at least one task running all the time.
                
                Alternatively you could publish application wide events and listen to them in the UI. In this case
                you would then be on your own with UI synchronization. Note, that e.g. Spring's events are synchronous
                by default, so it might make sense to wrap the actual UI updates with e.g. *uiFuture.runAsync(Runnable)*
                not to block other listeners receiving the event (until all UIs in the app are updated).               
                """));

        uiFuture = new UIFuture();
        grid = new TaskGrid(appWideTasks);
        var activeTasks = grid.listTasks();
        activeTasks.forEach(task -> {
            subscribeForResult(task);
        });

        grid.listTasks();

        IntegerField taskDuration = new IntegerField("Task duration (seconds)");
        taskDuration.setMin(1);
        taskDuration.setMax(60);
        taskDuration.setValue(15);
        add(new HorizontalFloatLayout(
                        taskDuration,
                        new Button("Start new task", event -> {
                            var task = appWideTasks.startTask("New task", taskDuration.getValue());
                            subscribeForResult(task);
                            grid.listTasks();
                        }),
                        new Button("Refresh tasks", event -> {
                            grid.listTasks();
                        })
                )
        );

        add(grid);
    }

    public VProgressBar getProgressBar(AppWideTasks.Task task) {
        return taskToProgressBar.computeIfAbsent(task, t -> {
            VProgressBar progressBar = new VProgressBar();
            progressBar.setPrepareForOverdueInAnimation(false); // This demo has good estimates
            return progressBar;
        });
    }

    private void subscribeForResult(AppWideTasks.Task task) {
        CompletableFuture<String> subscription = task.subscribe();
        taskToSubscribtion.put(task, subscription);
        uiFuture.of(subscription).thenAccept(result -> {
            Notification.show("Task completed: " + result);
            getProgressBar(task).finish();
            taskToSubscribtion.remove(task);
        });
    }

    public class TaskGrid extends VGrid<AppWideTasks.Task> {

        private final AppWideTasks appWideTasks;

        public TaskGrid(AppWideTasks appWideTasks) {
            super(AppWideTasks.Task.class);
            this.appWideTasks = appWideTasks;
            getColumnByKey("listeners").setVisible(false);

            addComponentColumn(task -> {
                VProgressBar progressBar = getProgressBar(task);
                progressBar.animateToEstimate(task.start(), task.start().plusSeconds(task.duration()));
                return progressBar;
            }).setHeader("Progress");
            addComponentColumn(task -> {
                var subscribedCheckbox = new Checkbox();
                subscribedCheckbox.setValue(taskToSubscribtion.containsKey(task));
                subscribedCheckbox.addValueChangeListener(event -> {
                    if (event.getValue()) {
                        subscribeForResult(task);
                    } else {
                        CompletableFuture<String> subscription = taskToSubscribtion.remove(task);
                        task.unSubscribe(subscription);
                        Notification.show("Unsubscribed of task events");
                    }
                    listTasks();
                });
                return subscribedCheckbox;
            }).setHeader("Subscribed for result");

            getColumns().forEach(column -> column.setAutoWidth(true));
        }

        public List<AppWideTasks.Task> listTasks() {
            List<AppWideTasks.Task> activeTasks = appWideTasks.getActiveTasks();
            setItems(activeTasks);
            return activeTasks;
        }
    }

}
