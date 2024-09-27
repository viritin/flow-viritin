package org.vaadin.firitin.testdomain;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class AppWideTasks {

    private static int nextId = 0;

    /**
     * A data structure for a task whose result can be listened using a {@link CompletableFuture}.
     *
     * @param name
     * @param start
     * @param duration
     * @param listeners
     */
    public record Task(int id, String name, LocalDateTime start, int duration, List<CompletableFuture<String>> listeners) {
        public Task(String name, LocalDateTime start, int duration) {
            this(nextId++, name, start, duration, new ArrayList<>());
        }

        public CompletableFuture<String> subscribe() {
            if(start.plusSeconds(duration).isBefore(LocalDateTime.now())) {
                return CompletableFuture.completedFuture("Task was alrelady completed :-)");
            }
            CompletableFuture<String> future = new CompletableFuture<>();
            listeners.add(future);
            return future;
        }

        public void unSubscribe(CompletableFuture<String> future) {
            listeners.remove(future);
        }

        public void complete(String result) {
            listeners.forEach(listener -> listener.complete(result));
        }

    }

    private List<Task> activeTasks = new ArrayList<>();

    public Task startTask(String name, int duration) {
        Task task = new Task(name, LocalDateTime.now(), duration);
        activeTasks.add(task);
        return task;
    }

    public List<Task> getActiveTasks() {
        return new ArrayList<>(activeTasks);
    }

    @Scheduled(fixedRate = 100)
    void maintainTasks() {
        List<Task> finished = activeTasks.stream().filter(task -> task.start.plusSeconds(task.duration).isBefore(LocalDateTime.now()))
                .toList();
        activeTasks.removeAll(finished);
        finished.forEach(task -> task.complete("Task " + task.name() + " completed at " + LocalTime.now()));


        if(activeTasks.isEmpty()) {
            // For the demo, make sure there is always at least one task running
            startTask("DemoTask", 15);
        }
    }

}
