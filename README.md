# mongo-lambda-query
a lambda-based object-oriented mongo query plug-in <br>
基于lambda表达式，且面向对象的mongo数据库查询插件。
*** 
### 使用步骤：
1. 引入maven依赖文件
    ```
        <dependency>
            <groupId>io.github.darmi7</groupId>
            <artifactId>mongo-lambda-query</artifactId>
            <version>1.1.0</version>
        </dependency>
2. 开启插件注解，加上需要扫描mongo实体的包路径
    ``` 
    @EnableMongoLambdaQuery
    @EnableMongoRepositories(basePackages = "com.darmi.demo.repository.mongo")
    public class Application
3. 编写查询代码
    ```
    @Repository
    public interface TaskRepository extends MongoRepository<Task, String> {
  
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
具体使用案例参考demo模块，如有什么问题欢迎留言，如果有用请为我点亮star。<br>
原理分析可以查看博客：https://blog.csdn.net/qq_28175019/article/details/129100748

