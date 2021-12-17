package com.goganesh.packages.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.goganesh.packages.domain.Site;
import com.goganesh.packages.dto.SearchResult;
import com.goganesh.packages.exception.ApiException;
import com.goganesh.packages.service.SearchService;
import com.goganesh.packages.service.SiteService;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class SearchController {

    private final static String SITE_URL_NOT_FOUND_ERROR = "Указанная страница не найдена";
    private final static String EMPTY_QUERY_ERROR = "Задан пустой поисковый запрос";

    private final SearchService searchService;
    private final SiteService siteService;

    @SneakyThrows
    @GetMapping(
            value = "/search",
            produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    public String search(@RequestParam String query,
                         @RequestParam(required = false) String siteUrl,
                         @RequestParam int offset,
                         @RequestParam int limit) {

        if (query.isBlank())
            throw new ApiException(EMPTY_QUERY_ERROR, HttpStatus.OK);

        Site site;
        List<SearchResult> searchResults;
        if (Objects.nonNull(siteUrl)) {
            site = siteService.findByUrl(siteUrl)
                    .orElseThrow(()-> new ApiException(SITE_URL_NOT_FOUND_ERROR, HttpStatus.OK));
            searchResults = searchService.getSearchResultBySite(query, site);
        } else {
            searchResults = siteService.findAll()
                    .stream()
                    .filter(s -> s.getStatus().equals(Site.Status.INDEXED))
                    .map(s -> searchService.getSearchResultBySite(query, s))
                    .flatMap(List::stream)
                    .sorted((o1, o2)->o2.getRelevance().
                            compareTo(o1.getRelevance()))
                    .collect(Collectors.toList());
        }

        List<SearchResult> page = searchResults.subList(offset, Math.min((offset + limit), searchResults.size()));

        Map<String, Object> result = new HashMap<>();
        result.put("result", true);
        result.put("count", searchResults.size());
        result.put("data", page);

        return new ObjectMapper().writeValueAsString(result);
    }
}
