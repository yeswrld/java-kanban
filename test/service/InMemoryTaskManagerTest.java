package service;

import com.tasktracker.app.service.InMemoryTaskManager;
import com.tasktracker.app.service.Managers;
import com.tasktracker.app.service.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;


@DisplayName("Проверка InMemoryTaksManager")
class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    void taskManagerInit() {
        taskManager = Managers.getMemoryManager();
    }


}