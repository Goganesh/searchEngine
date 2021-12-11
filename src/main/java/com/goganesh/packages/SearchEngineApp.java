package com.goganesh.packages;

import com.goganesh.packages.domain.Index;
import com.goganesh.packages.domain.Lemma;
import com.goganesh.packages.domain.Page;
import com.goganesh.packages.domain.Site;
import com.goganesh.packages.dto.PageDto;
import com.goganesh.packages.exception.BadBaseUrlException;
import com.goganesh.packages.repository.IndexRepository;
import com.goganesh.packages.repository.LemmaRepository;
import com.goganesh.packages.service.*;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@SpringBootApplication
@AllArgsConstructor
public class SearchEngineApp implements CommandLineRunner {
    private final PageService pageService;
    private final SiteService siteService;
    private final LemmaRepository lemmaRepository;
    private final LemmaService lemmaService;
    private final IndexRepository indexRepository;
    private final WebParser webParser;

    private static final Logger LOGGER = LoggerFactory.getLogger(SearchEngineApp.class);

    public static void main(String[] args) {
        SpringApplication.run(SearchEngineApp.class, args);
    }

    @Override
    public void run(String... args) {
        siteService.findAll().forEach(siteService::parseSite);
        siteService.findAll().forEach(System.out::println);

        siteService.findAll().forEach(siteService::indexSite);

        siteService.findAll().forEach(System.out::println);


        //String searchText = "Наличие сообщества науки";
        //List<PageDto> searchResult = findPagesDtoBySearchText(searchText);
        //searchResult.forEach(System.out::println);
    }

    private List<PageDto> findPagesDtoBySearchText(String searchText) throws IOException {
        Set<String> searchLemmas = lemmaService.getLemmasByText(searchText);
        List<Lemma> lemmas = searchLemmas.stream()
                .map(lemmaService::findByLemma)
                .filter(Objects::nonNull)
                .sorted(Comparator.comparingInt(Lemma::getFrequency))
                .collect(Collectors.toList());

        Map<Page, Integer> pagesCount = new HashMap<>();

        for (Lemma lemma : lemmas) {
            indexRepository.findByLemma(lemma)
                    .stream()
                    .map(Index::getPage)
                    .forEach(page -> pagesCount.merge(page, 1, Integer::sum));
        }

        Set<Page> finalPages = new HashSet<>();
        for (Map.Entry<Page, Integer> entry : pagesCount.entrySet()){
            if (entry.getValue().equals(lemmas.size())) {
                System.out.println("страница " + entry.getKey().getPath() + " , кол-во " + entry.getValue());
                finalPages.add(entry.getKey());
            }
        }

        if (finalPages.isEmpty())
            return new ArrayList<>();

        finalPages.forEach(System.out::println);

        List<PageDto> rankedPages = new ArrayList<>();
        for (Page page : finalPages) {
            Float finalRank = indexRepository.findByPage(page)
                    .stream()
                    .filter(index -> lemmas.contains(index.getLemma()))
                    .map(Index::getRank)
                    .reduce(Float::sum)
                    .orElse(0f);

            PageDto pageDto = convertToDto(page);
            pageDto.setRelevanceAbs(finalRank);

            rankedPages.add(pageDto);
        }
        return rankedPages;
    }

    private PageDto convertToDto(Page page){
        PageDto pageDto = new PageDto();

        pageDto.setTitle(webParser.getTextByPageAndField(page,"title"));
        pageDto.setUri(page.getPath());
        //pageDto.setSnippet();
        //pageDto.setRelevanceAbs();
        //pageDto.setRelevanceRel();

        return pageDto;
    }
}
