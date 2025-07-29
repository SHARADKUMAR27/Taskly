
package com.Taskly.dto;

import com.Taskly.model.Todo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TodoDTO {

    private Long id;

    @NotBlank(message = "Title is required")
    @Size(max = 100, message = "Title must be less than 100 characters")
    private String title;

    @Size(max = 500, message = "Description must be less than 500 characters")
    private String description;

    private boolean completed;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime dueDate;

    private Todo.Priority priority;

    // Conversion methods
    public static TodoDTO fromEntity(Todo todo) {
        return TodoDTO.builder()
                .id(todo.getId())
                .title(todo.getTitle())
                .description(todo.getDescription())
                .completed(todo.isCompleted())
                .createdAt(todo.getCreatedAt())
                .updatedAt(todo.getUpdatedAt())
                .dueDate(todo.getDueDate())
                .priority(todo.getPriority())
                .build();
    }

    public Todo toEntity() {
        return Todo.builder()
                .id(this.id)
                .title(this.title)
                .description(this.description)
                .completed(this.completed)
                .dueDate(this.dueDate)
                .priority(this.priority)
                .build();
    }
}