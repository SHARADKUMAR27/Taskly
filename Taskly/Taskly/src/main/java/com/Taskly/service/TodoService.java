package com.Taskly.service;

import com.Taskly.dto.TodoDTO;
import com.Taskly.model.Todo;
import com.Taskly.model.User;
import com.Taskly.repository.TodoRepository;
import com.Taskly.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TodoService {

    private final TodoRepository todoRepository;
    private final UserRepository userRepository;

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }

    public List<TodoDTO> getAllTodos() {
        User currentUser = getCurrentUser();
        return todoRepository.findByUser(currentUser).stream()
                .map(TodoDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public TodoDTO getTodoById(Long id) {
        User currentUser = getCurrentUser();
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Todo not found with id: " + id));

        // Make sure the todo belongs to the current user
        if (!todo.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Access denied: Todo belongs to another user");
        }

        return TodoDTO.fromEntity(todo);
    }

    public TodoDTO createTodo(TodoDTO todoDTO) {
        User currentUser = getCurrentUser();

        Todo todo = todoDTO.toEntity();
        todo.setUser(currentUser);
        todo.setCreatedAt(LocalDateTime.now());
        todo.setUpdatedAt(LocalDateTime.now());

        Todo savedTodo = todoRepository.save(todo);
        return TodoDTO.fromEntity(savedTodo);
    }

    public TodoDTO updateTodo(Long id, TodoDTO todoDTO) {
        User currentUser = getCurrentUser();

        Todo existingTodo = todoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Todo not found with id: " + id));

        // Make sure the todo belongs to the current user
        if (!existingTodo.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Access denied: Todo belongs to another user");
        }

        // Update fields
        existingTodo.setTitle(todoDTO.getTitle());
        existingTodo.setDescription(todoDTO.getDescription());
        existingTodo.setCompleted(todoDTO.isCompleted());
        existingTodo.setDueDate(todoDTO.getDueDate());
        existingTodo.setPriority(todoDTO.getPriority());
        existingTodo.setUpdatedAt(LocalDateTime.now());

        Todo updatedTodo = todoRepository.save(existingTodo);
        return TodoDTO.fromEntity(updatedTodo);
    }

    public void deleteTodo(Long id) {
        User currentUser = getCurrentUser();

        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Todo not found with id: " + id));

        // Make sure the todo belongs to the current user
        if (!todo.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Access denied: Todo belongs to another user");
        }

        todoRepository.deleteById(id);
    }

    public List<TodoDTO> getCompletedTodos() {
        User currentUser = getCurrentUser();
        return todoRepository.findByUserAndCompleted(currentUser, true).stream()
                .map(TodoDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<TodoDTO> getPendingTodos() {
        User currentUser = getCurrentUser();
        return todoRepository.findByUserAndCompleted(currentUser, false).stream()
                .map(TodoDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<TodoDTO> getTodosByPriority(Todo.Priority priority) {
        User currentUser = getCurrentUser();
        return todoRepository.findByUserAndPriority(currentUser, priority).stream()
                .map(TodoDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<TodoDTO> getUpcomingTodos() {
        User currentUser = getCurrentUser();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime weekFromNow = now.plusDays(7);

        return todoRepository.findByUser(currentUser).stream()
                .filter(todo -> {
                    LocalDateTime dueDate = todo.getDueDate();
                    return dueDate != null && dueDate.isAfter(now) && dueDate.isBefore(weekFromNow);
                })
                .map(TodoDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<TodoDTO> getOverdueTodos() {
        User currentUser = getCurrentUser();
        LocalDateTime now = LocalDateTime.now();

        return todoRepository.findByUserAndCompleted(currentUser, false).stream()
                .filter(todo -> {
                    LocalDateTime dueDate = todo.getDueDate();
                    return dueDate != null && dueDate.isBefore(now);
                })
                .map(TodoDTO::fromEntity)
                .collect(Collectors.toList());
    }
}