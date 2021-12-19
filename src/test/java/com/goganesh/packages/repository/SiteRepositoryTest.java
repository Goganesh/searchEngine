package com.goganesh.packages.repository;

import com.goganesh.packages.domain.Site;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DisplayName("Тестирование кастомных запросов - SiteRepository")
class SiteRepositoryTest {

    @Autowired
    private SiteRepository siteRepository;

    @BeforeEach
    void setUp() {
        List<Site> siteList = new ArrayList<>();
        siteList.add(Site.builder()
                .url("https://www.baeldung.com")
                .build());

        siteList.add(Site.builder()
                .url("https://github.com")
                .build());

        siteList.add(Site.builder()
                .url("https://springdoc.org")
                .build());

        siteList.add(Site.builder()
                .url("https://myresult.ru")
                .build());

        siteRepository.saveAll(siteList);
    }

    @Test
    @DisplayName("Запрос сайта по LIKE в наименовании урла: протокол + хост")
    void findByHost() {
        assertAll(
                () -> siteRepository.findByHost("https://myresult.ru").get().getUrl().equals("https://myresult.ru"),
                () -> siteRepository.findByHost("http://myresult.ru").isEmpty());
        ;
    }
}