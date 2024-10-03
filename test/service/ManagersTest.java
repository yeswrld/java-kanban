package service;

import com.tasktracker.app.service.Managers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Проверка Managers")
class ManagersTest {

    @Test
    void createDefaultManager() { //убедитесь, что утилитарный класс всегда возвращает проинициализированные и готовые к работе экземпляры менеджеров;
        Assertions.assertNotNull(Managers.getDefault());
    }

    @Test
    void createDefaultHistoryManager() { //убедитесь, что утилитарный класс всегда возвращает проинициализированные и готовые к работе экземпляры менеджеров;
        Assertions.assertNotNull(Managers.getDefaultHistory());
    }

}
