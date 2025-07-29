package com.Taskly.repository;

import com.Taskly.model.Todo;
import com.Taskly.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {

    List<Todo> findByUser(User user);

    List<Todo> findByUserAndCompleted(User user, boolean completed);

    List<Todo> findByUserOrderByDueDateAsc(User user);

    List<Todo> findByUserAndDueDateBefore(User user, LocalDateTime dueDate);

    List<Todo> findByUserAndPriority(User user, Todo.Priority priority);
}