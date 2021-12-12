package com.goganesh.packages;

import com.goganesh.packages.service.*;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
@AllArgsConstructor
public class SearchEngineApp implements CommandLineRunner {
    private final SiteService siteService;
    private final SearchService searchService;
    private final IndexService indexService;

    private static final Logger LOGGER = LoggerFactory.getLogger(SearchEngineApp.class);

    public static void main(String[] args) {
        SpringApplication.run(SearchEngineApp.class, args);
    }

    @Override
    public void run(String... args) {
    }

}
