package com.goganesh.packages.repository;

import com.goganesh.packages.domain.Index;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DisplayName("Тестирование кастомных запросов в IndexRepository")
class IndexRepositoryTest {

    @Autowired
    private IndexRepository indexRepository;


    @BeforeEach
    void setUp() {
        List<Index> indexList = new ArrayList<>();
        for (int i = 0; i < 4; i++){
            indexList.add(Index.builder()
                    .lemma("лемма1")
                    .build());
        }

        indexList.add(Index.builder()
                .lemma("лемма2")
                .build());

        indexRepository.saveAll(indexList);
    }

    @Test
    @DisplayName("Запрос уникальных лемм среди всех индексов")
    void countLemmas() {
        long actual = indexRepository.countLemmas();
        long expected = 2L;

        assertEquals(expected, actual);
    }
}