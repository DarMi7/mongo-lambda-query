package com.darmi.demo.repository.mongo;

import com.darmi.demo.request.TaskCriteria;
import com.darmi.demo.entity.mongo.Task;
import com.darmi.plugin.core.MongoLambdaQuery;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @author darmi
 */
@Repository
public interface TaskRepository extends MongoRepository<Task, String> {

    default Task one(TaskCriteria taskCriteria) {
        return MongoLambdaQuery.lambdaQuery(Task.class)
            .is(Task::getName, taskCriteria.getName())
            .one();
    }

    default List<Task> list(TaskCriteria taskCriteria) {
        return MongoLambdaQuery.lambdaQuery(Task.class)
            .gt(Task::getCreated, taskCriteria.getBegin())
            .lt(Task::getCreated, taskCriteria.getEnd())
            .list();
    }


    default Page<Task> search(TaskCriteria taskCriteria) {
        return MongoLambdaQuery.lambdaQuery(Task.class)
                .is(Task::getName, taskCriteria.getName())
                .is(Task::getType, taskCriteria.getFuzzyName())
                .reg(Task::getName, taskCriteria.getName())
                .gt(Task::getPoints, taskCriteria.getPoints())
                .gt(Task::getCreated, taskCriteria.getBegin())
                .lt(Task::getCreated, taskCriteria.getEnd())
                .page(taskCriteria.getPagination());
    }

    default Page<Task> aggregate(TaskCriteria taskCriteria) {
        return MongoLambdaQuery.lambdaQuery(Task.class)
            .is(Task::getName, taskCriteria.getName())
            .is(Task::getType, taskCriteria.getFuzzyName())
            .reg(Task::getName, taskCriteria.getName())
            .gt(Task::getPoints, taskCriteria.getPoints())
            .gt(Task::getCreated, taskCriteria.getBegin())
            .lt(Task::getCreated, taskCriteria.getEnd())
            .aggregate(taskCriteria.getPagination());
    }
}
