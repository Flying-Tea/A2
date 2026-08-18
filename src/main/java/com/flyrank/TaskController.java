package com.flyrank;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TaskController {
    private final JdbcTemplate jdbc;

    // Sqlite
    public TaskController(JdbcTemplate jdbc) {
        this.jdbc = jdbc;

        // Create the table if it doesn't exist
        jdbc.execute("""
            CREATE TABLE IF NOT EXISTS tasks (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                title TEXT NOT NULL,
                done INTEGER NOT NULL DEFAULT 0
            )
        """);

        // Add example tasks only if database is empty
        seedTasks();
    }

    private void seedTasks() {

        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM tasks",
                Integer.class
        );

        if (count == 0) {
            jdbc.update(
                    "INSERT INTO tasks (title, done) VALUES (?, ?)",
                    "Sleep In", 0
            );

            jdbc.update(
                    "INSERT INTO tasks (title, done) VALUES (?, ?)",
                    "Stop being busy", 1
            );

            jdbc.update(
                    "INSERT INTO tasks (title, done) VALUES (?, ?)",
                    "Finish FlyRank", 0
            );
        }
    }

    // Stage 1

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

    // Stage 2

    @GetMapping("/tasks")
    public List<Task> getTasks() {

        return jdbc.query(
                "SELECT id, title, done FROM tasks",
                (rs, rowNum) -> new Task(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getInt("done") == 1
                )
        );
    }

    @GetMapping("/tasks/{id}")
    public ResponseEntity<?> getTask(@PathVariable int id) {

        List<Task> result = jdbc.query(
                "SELECT id, title, done FROM tasks WHERE id = ?",
                (rs, rowNum) -> new Task(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getInt("done") == 1
                ),
                id
        );

        if (result.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Task " + id + " not found"));
        }

        return ResponseEntity.ok(result.get(0));
    }

    // Stage 3

    @PostMapping("/tasks")
    public ResponseEntity<?> createTask(@RequestBody Task newTask) {

        if (newTask.getTitle() == null ||
                newTask.getTitle().isBlank()) {

            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", "Title is required"));
        }

        jdbc.update(
                "INSERT INTO tasks (title, done) VALUES (?, ?)",
                newTask.getTitle(),
                0
        );

        Integer id = jdbc.queryForObject(
                "SELECT last_insert_rowid()",
                Integer.class
        );

        Task createdTask = new Task(
                id,
                newTask.getTitle(),
                false
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdTask);
    }

    // Stage 4

    @PutMapping("/tasks/{id}")
    public ResponseEntity<?> updateTask(
            @PathVariable int id,
            @RequestBody Map<String, Object> body) {

        if (body.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", "Request body cannot be empty"));
        }

        List<Task> result = jdbc.query(
                "SELECT id, title, done FROM tasks WHERE id = ?",
                (rs, rowNum) -> new Task(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getInt("done") == 1
                ),
                id
        );

        if (result.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Task " + id + " not found"));
        }

        Task task = result.get(0);

        if (body.containsKey("title")) {

            Object titleValue = body.get("title");

            if (!(titleValue instanceof String title) ||
                    title.isBlank()) {

                return ResponseEntity
                        .badRequest()
                        .body(Map.of(
                                "error",
                                "Title must be a non-empty string"
                        ));
            }

            task.setTitle(title);
        }

        if (body.containsKey("done")) {

            Object doneValue = body.get("done");

            if (!(doneValue instanceof Boolean done)) {

                return ResponseEntity
                        .badRequest()
                        .body(Map.of(
                                "error",
                                "Done must be true or false"
                        ));
            }

            task.setDone(done);
        }

        jdbc.update(
                "UPDATE tasks SET title = ?, done = ? WHERE id = ?",
                task.getTitle(),
                task.isDone() ? 1 : 0,
                id
        );

        return ResponseEntity.ok(task);
    }

    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable int id) {

        int rowsDeleted = jdbc.update(
                "DELETE FROM tasks WHERE id = ?",
                id
        );

        if (rowsDeleted == 0) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "error",
                            "Task " + id + " not found"
                    ));
        }

        return ResponseEntity.noContent().build();
    }
}