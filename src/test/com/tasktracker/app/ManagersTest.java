package test.com.tasktracker.app;

import com.tasktracker.app.service.Managers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ManagersTest {
    @Test
    void createDefaultManager() { //убедитесь, что утилитарный класс всегда возвращает проинициализированные и готовые к работе экземпляры менеджеров;
        Assertions.assertNotNull(Managers.getDefault());
    }

    @Test
    void createDefaultHistoryManager() { //убедитесь, что утилитарный класс всегда возвращает проинициализированные и готовые к работе экземпляры менеджеров;
        Assertions.assertNotNull(Managers.getDefaultHistory());
    }

    @Test
    void createManagerAndHistoryManager() {
        Assertions.assertNotNull(Managers.getDefault());
        Assertions.assertNotNull(Managers.getDefaultHistory());
    }

}
