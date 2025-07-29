package com.Taskly.controller;

import com.Taskly.dto.TodoDTO;
import com.Taskly.model.Todo;
import com.Taskly.service.TodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/todos")
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;

    @GetMapping
    public ResponseEntity<List<TodoDTO>> getAllTodos() {
        return ResponseEntity.ok(todoService.getAllTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TodoDTO> getTodoById(@PathVariable Long id) {
        return ResponseEntity.ok(todoService.getTodoById(id));
    }

    @PostMapping
    public ResponseEntity<TodoDTO> createTodo(@Valid @RequestBody TodoDTO todoDto) {
        return new ResponseEntity<>(todoService.createTodo(todoDto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TodoDTO> updateTodo(@PathVariable Long id, @Valid @RequestBody TodoDTO todoDto) {
        return ResponseEntity.ok(todoService.updateTodo(id, todoDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTodo(@PathVariable Long id) {
        todoService.deleteTodo(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/completed")
    public ResponseEntity<List<TodoDTO>> getCompletedTodos() {
        return ResponseEntity.ok(todoService.getCompletedTodos());
    }

    @GetMapping("/pending")
    public ResponseEntity<List<TodoDTO>> getPendingTodos() {
        return ResponseEntity.ok(todoService.getPendingTodos());
    }

    @GetMapping("/priority/{priority}")
    public ResponseEntity<List<TodoDTO>> getTodosByPriority(@PathVariable Todo.Priority priority) {
        return ResponseEntity.ok(todoService.getTodosByPriority(priority));
    }

    @GetMapping("/upcoming")
    public ResponseEntity<List<TodoDTO>> getUpcomingTodos() {
        return ResponseEntity.ok(todoService.getUpcomingTodos());
    }

    @GetMapping("/overdue")
    public ResponseEntity<List<TodoDTO>> getOverdueTodos() {
        return ResponseEntity.ok(todoService.getOverdueTodos());
    }
}