package ru.yandex.practicum.tasktracker.service;

import org.junit.jupiter.api.BeforeEach;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    public void beforeEach() {
       taskManager = Managers.getDefault();
    }
}