package com.goganesh.packages.service;

import com.goganesh.packages.exception.LemmaException;
import com.goganesh.packages.repository.IndexRepository;
import com.goganesh.packages.service.implementation.IndexServiceImpl;
import com.goganesh.packages.service.implementation.LemmaServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("Тестирование получение лемм - LemmaService")
@Import({LemmaServiceImpl.class, IndexServiceImpl.class})
class LemmaServiceTest {

    private static final String TEST_TEXT = "Повторное появление леопарда в Осетии позволяет предположить, что\n" +
            "леопард постоянно обитает в некоторых районах Северного Кавказа.";

    @Autowired
    private LemmaService lemmaService;

    @Autowired
    private IndexService indexService;

    @MockBean
    private IndexRepository indexRepository;

    @Configuration
    static class TestConfig {

    }

    @Test
    @DisplayName("Получение исключения при пустом текста")
    void throwExcpetionByEmptyText() {
        assertAll(
                ()->assertThrows(LemmaException.class, () -> lemmaService.getLemmasCountByText("")),
                ()->assertThrows(LemmaException.class, () -> lemmaService.getLemmasByText(""))
        );
    }

    @Test
    @DisplayName("Получение уникальных лемм из текста")
    void getLemmasByText() throws IOException {

        Set<String> expected = new HashSet<>(){{
            add("повторный");
            add("некоторый");
            add("появление");
            add("постоянно");
            add("постоянный");
            add("некоторые");
            add("позволять");
            add("северный");
            add("предположить");
            add("кавказ");
            add("район");
            add("осетия");
            add("обитать");
            add("леопард");
        }};

        Set<String> actual = lemmaService.getLemmasByText(TEST_TEXT);

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Получение кол-ва лемм из текста")
    void getLemmasCountByText() throws IOException {
        Map<String, Integer> expected = new HashMap<>(){{
            put("повторный", 1);
            put("некоторый", 1);
            put("появление", 1);
            put("постоянно", 1);
            put("постоянный", 1);
            put("некоторые", 1);
            put("позволять", 1);
            put("северный", 1);
            put("предположить", 1);
            put("кавказ", 1);
            put("район", 1);
            put("осетия", 1);
            put("обитать", 1);
            put("леопард", 2);
        }};

        Map<String, Integer> actual = lemmaService.getLemmasCountByText(TEST_TEXT);

        assertEquals(expected, actual);
    }
}