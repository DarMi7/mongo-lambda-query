# mongo-lambda-query
a lambda-based object-oriented mongo query plug-in <br>
基于lambda表达式，且面向对象的mongo数据库查询插件。<br>
****先赞后看，🌟🌟🌟**** <br>
****Like before you read,🌟🌟🌟****
### 使用步骤：
1. 引入maven依赖文件<br>
   Importing maven dependency files.
    ```
        <dependency>
            <groupId>io.github.darmi7</groupId>
            <artifactId>mongo-lambda-query</artifactId>
            <version>1.3.0</version>
        </dependency>
3. 开启插件注解，加上需要扫描mongo实体的包路径<br>
   Open plug-in annotations, plus the package path that requires scanning mongo entities.
    ``` 
    @EnableMongoLambdaQuery
    @EnableMongoRepositories(basePackages = "com.darmi.demo.repository.mongo")
    public class Application
5. 编写查询代码<br>
   Write query code
    ```
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
        }
7. 具体使用案例参考demo模块，如有什么问题欢迎留言。<br>
For details, refer to the demo module. If you have any questions, please leave a message.<br>
原理分析可以查看博客：https://blog.csdn.net/qq_28175019/article/details/129100748

