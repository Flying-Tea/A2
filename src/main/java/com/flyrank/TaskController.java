package com.flyrank;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class TaskController {
    
    private final List<Task> tasks = new ArrayList<>();
    
    public TaskController() {
        tasks.add(new Task(1, "Sleep In", false));
        tasks.add(new Task(2, "Stop being busy", true));
        tasks.add(new Task(3, "Finish FlyRank", false));
    }

    //Stage 1
    @GetMapping("/")
    public Map<String, Object> home() {
        return Map.of(
                "name", "Task API",
                "version", "1.0",
                "endpoints", List.of("/tasks")
        );
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "ok");
    }

    //Stage 2

    @GetMapping("/tasks")
    public List<Task> getTasks() {
        return tasks;
    }

    @GetMapping("/tasks/{id}")
    public ResponseEntity<?> getTask(@PathVariable int id) {

        for (Task task : tasks) {
            if (task.getId() == id) {
                return ResponseEntity.ok(task);
            }
        }

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Task " + id + " not found"));
    }
}