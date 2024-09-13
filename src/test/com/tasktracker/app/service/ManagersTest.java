package com.tasktracker.app.service;

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

}
