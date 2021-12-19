package com.goganesh.packages.service;

import com.goganesh.packages.exception.ActiveProcessException;
import com.goganesh.packages.service.implementation.ProcessServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Import({ProcessServiceImpl.class})
@DisplayName("Тестирование блокировки процесса - ProcessService")
class ProcessServiceTest {

    @Autowired
    private ProcessService processService;

    @Configuration
    static class TestConfig {

    }

    @AfterEach
    void tearDown() {
        if (processService.isProcessActive(ProcessService.Type.INDEX))
            processService.unblockProcess(ProcessService.Type.INDEX);
    }

    @Test
    @DisplayName("Успешная блокировка процесса")
    void successBlockProcess() {
        processService.blockProcess(ProcessService.Type.INDEX);
        assertTrue(true);
    }

    @Test
    @DisplayName("Не успешная блокировка процесса")
    void errorBlockProcess() {
        processService.blockProcess(ProcessService.Type.INDEX);
        assertThrows(ActiveProcessException.class, () -> processService.blockProcess(ProcessService.Type.INDEX));
    }

    @Test
    @DisplayName("Успешная разблокировка процесса")
    void unblockProcess() {
        processService.blockProcess(ProcessService.Type.INDEX);
        processService.unblockProcess(ProcessService.Type.INDEX);
        assertTrue(true);
    }

    @Test
    @DisplayName("Не успешная разблокировка процесса")
    void isProcessActive() {
        assertThrows(ActiveProcessException.class, () -> processService.unblockProcess(ProcessService.Type.INDEX));
    }
}