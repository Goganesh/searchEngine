package com.goganesh.packages.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.goganesh.packages.domain.Index;
import com.goganesh.packages.domain.Site;
import com.goganesh.packages.dto.Detailed;
import com.goganesh.packages.dto.SearchResult;
import com.goganesh.packages.dto.Statistics;
import com.goganesh.packages.dto.Total;
import com.goganesh.packages.exception.ActiveProcessException;
import com.goganesh.packages.service.*;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class ApiController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiController.class);

    private static final String ACTIVE_PROCESS_ERROR = "Индексация уже запущена";
    private static final String INACTIVE_PROCESS_ERROR = "Индексация не запущена";

    private final ProcessService processService;
    private final SiteService siteService;
    private final PageService pageService;
    private final IndexService indexService;
    private final LemmaService lemmaService;
    private final SearchService searchService;

    @SneakyThrows
    @GetMapping("/startIndexing")
    public String startIndexing() {
        ObjectMapper objectMapper = new ObjectMapper();

        Map<String, Object> result = new HashMap<>();
        result.put("result", true);

        try {
            processService.blockProcess(ProcessService.Type.INDEX);
        } catch (ActiveProcessException e) {
            result.put("result", false);
            result.put("error", ACTIVE_PROCESS_ERROR);

            return objectMapper.writeValueAsString(result);
        }

        new Thread(() -> {
            siteService.findAll()
                    .forEach(site -> {
                        if (site.getStatus().equals(Site.Status.NEW)) {
                            siteService.parseSite(site);
                            siteService.indexSite(site);
                        } else if (site.getStatus().equals(Site.Status.INDEXED)) {
                            siteService.dropIndexSite(site);
                            siteService.indexSite(site);
                        } else {
                            LOGGER.debug("Site does not index - " + site);
                        }
                    });

            processService.unblockProcess(ProcessService.Type.INDEX);
        }).start();

        return objectMapper.writeValueAsString(result);
    }

    @GetMapping("/stopIndexing")
    public String stopIndexing() {
        //todo
        //Метод останавливает текущий процесс индексации (переиндексации).
                //Если в настоящий момент индексация или
        //переиндексация не происходит, метод возвращает соответствующее
        //сообщение об ошибке.

        return "somedata";
    }

    @PostMapping("/indexPage")
    public String indexPage() {
        //todo
        //Метод добавляет в индекс или обновляет отдельную страницу,
        //адрес которой передан в параметре.
        //      Если адрес страницы передан неверно, метод должен вернуть
        //соответствующую ошибку.
        return "somedata";
    }

    @SneakyThrows
    @GetMapping("/statistics")
    public String getStatistics() {
        Map<String, Object> result = new HashMap<>(){{
            put("result", true);
        }};

        Total total = Total.builder()
                .isIndexing(processService.isProcessActive(ProcessService.Type.INDEX))
                .lemmas(lemmaService.countLemmas())
                .pages(pageService.countPages())
                .sites(siteService.countSites())
                .build();

        List<Detailed> details = siteService.findAll()
                .stream()
                .map(site -> {
                    return Detailed.builder()
                            .url(site.getUrl())
                            .name(site.getName())
                            .status(site.getStatus().toString())
                            .statusTime(site.getStatusTime().toEpochSecond(ZoneOffset.UTC))
                            .error(site.getLastError())
                            .pages(site.getPages().size())
                            .lemmas(site.getPages()
                                    .stream()
                                    .map(indexService::findByPage)
                                    .flatMap(List::stream)
                                    .map(Index::getLemma)
                                    .distinct()
                                    .count())
                            .build();
                })
                .collect(Collectors.toList());

        Statistics statistics = Statistics.builder()
                .detailed(details)
                .total(total)
                .build();

        result.put("statistics", statistics);

        return new ObjectMapper().writeValueAsString(result);
    }

    @SneakyThrows
    @GetMapping("/search")
    public String search(@RequestParam String query,
                         @RequestParam(required = false) String site,
                         @RequestParam int offset,
                         @RequestParam int limit) {
        //todo поиск по сайту
        List<SearchResult> searchResults = searchService.getSearchResult(query);

        Map<String, Object> result = new HashMap<>(){{
            put("result", true);
        }};
        result.put("count", searchResults.size());
        result.put("data", searchResults);
        //todo пагинация

        return new ObjectMapper().writeValueAsString(result);
    }

}
