package com.goganesh.packages;

import com.goganesh.packages.service.*;
import lombok.AllArgsConstructor;
import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@SpringBootApplication
@AllArgsConstructor
public class SearchEngineApp implements CommandLineRunner {
    private final PageService pageService;
    private final PageSourceService pageSourceService;

    private static final Logger LOGGER = LoggerFactory.getLogger(SearchEngineApp.class);

    public static void main(String[] args) {
        SpringApplication.run(SearchEngineApp.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        LuceneMorphology luceneMorph =
                new RussianLuceneMorphology();
        List<String> wordBaseForms =
                luceneMorph.getNormalForms("леса");
        wordBaseForms.forEach(System.out::println);

        /*List<String> pagesSource = pageSourceService.getPagesSource();
        for (String source : pagesSource) {
            Set<Page> checkedPages;
            try{
                checkedPages = pageService.parseAllPagesByUrl(source);
            } catch (BadBaseUrlException badBaseUrlException) {
                LOGGER.error("Bad base URL - " + source);
                continue;
            }
            List<Page> pages = pageService.saveAll(checkedPages);
            pageService.findAll().forEach(System.out::println);
        }*/
    }
}
