package com.goganesh.packages.service.implementation;

import com.goganesh.packages.domain.Index;
import com.goganesh.packages.domain.Page;
import com.goganesh.packages.domain.Site;
import com.goganesh.packages.dto.SearchResult;
import com.goganesh.packages.service.IndexService;
import com.goganesh.packages.service.LemmaService;
import com.goganesh.packages.service.SearchService;
import com.goganesh.packages.service.WebParser;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class SearchServiceImpl implements SearchService {

    private final LemmaService lemmaService;
    private final IndexService indexService;
    private final WebParser webParser;

    @SneakyThrows
    @Override
    public List<SearchResult> getSearchResultBySite(String searchText, Site site) {
        Set<String> searchLemmas = lemmaService.getLemmasByText(searchText);

        Map<String, Integer> lemmasFrequency = lemmaService.getLemmasFrequency(searchLemmas);

        //todo Кроме того, рекомендуется исключать леммы, которые встречаются на слишком
        // большом количестве страниц (определите этот процент самостоятельно).

        List<String> sortedLemmas = lemmasFrequency
                .entrySet()
                .stream()
                .sorted(Comparator.comparing(Map.Entry::getValue))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        Map<Page, Integer> pagesCount = new HashMap<>();

        sortedLemmas.stream()
                .map(indexService::findByLemma)
                .flatMap(List::stream)
                .map(Index::getPage)
                .filter(page -> page.getSite().equals(site))
                .forEach(page -> pagesCount.merge(page, 1, Integer::sum));

        Set<Page> finalPages = pagesCount.entrySet()
                .stream()
                .filter(entry -> entry.getValue().equals(sortedLemmas.size()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        if (finalPages.isEmpty())
            return new ArrayList<>();

        List<SearchResult> rankedPages = new ArrayList<>();

        for (Page page : finalPages) {
            Float finalRank = indexService.findByPage(page)
                    .stream()
                    .filter(index -> sortedLemmas.contains(index.getLemma()))
                    .map(Index::getRank)
                    .reduce(Float::sum)
                    .orElse(0f);

            SearchResult searchResult = SearchResult.builder()
                    .uri(page.getPath())
                    .siteName(page.getSite().getName())
                    .site(page.getSite().getUrl())
                    .title(webParser.getTextByPageAndField(page,"title"))
                    .snippet("TODO") //todo требуется выполнить
                    .relevance(finalRank) //todo требуется выполнить
                    .build();
            searchResult.setRelevance(finalRank);

            rankedPages.add(searchResult);
        }

        return rankedPages;
    }

}
