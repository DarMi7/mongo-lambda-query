package com.darmi.demo.controller;

import com.darmi.demo.entity.mongo.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.darmi.demo.repository.mongo.TaskRepository;
import com.darmi.demo.request.TaskCriteria;

import java.util.List;

/**
 * @author darmi
 */
@RestController
@RequestMapping("/tasks")
public class TaskController {
    @Autowired
    private TaskRepository taskRepository;

    @PostMapping("/search")
    public List<Task> serch(@RequestBody @Validated TaskCriteria taskCriteria) {
        return taskRepository.search(taskCriteria).getContent();
    }
}
