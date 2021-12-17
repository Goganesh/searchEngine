package com.goganesh.packages.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.goganesh.packages.domain.Index;
import com.goganesh.packages.dto.Detailed;
import com.goganesh.packages.dto.Statistics;
import com.goganesh.packages.dto.Total;
import com.goganesh.packages.service.*;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class StatisticsController {

    private final PageService pageService;
    private final IndexService indexService;
    private final LemmaService lemmaService;
    private final ProcessService processService;
    private final SiteService siteService;

    @SneakyThrows
    @GetMapping(
            value = "/statistics",
            produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    public String getStatistics() {

        Total total = Total.builder()
                .isIndexing(processService.isProcessActive(ProcessService.Type.INDEX))
                .lemmas(lemmaService.countLemmas())
                .pages(pageService.countPages())
                .sites(siteService.countSites())
                .build();

        List<Detailed> details = siteService.findAll()
                .stream()
                .map(site -> Detailed.builder()
                        .url(site.getUrl())
                        .name(site.getName())
                        .status(site.getStatus().toString())
                        .statusTime(site.getStatusTime().toInstant(ZoneOffset.UTC).toEpochMilli())
                        .error(site.getLastError())
                        .pages(site.getPages().size())
                        .lemmas(site.getPages()
                                .stream()
                                .map(indexService::findByPage)
                                .flatMap(List::stream)
                                .map(Index::getLemma)
                                .distinct()
                                .count())
                        .build())
                .collect(Collectors.toList());

        Statistics statistics = Statistics.builder()
                .detailed(details)
                .total(total)
                .build();

        Map<String, Object> result = new HashMap<>();
        result.put("result", true);
        result.put("statistics", statistics);

        return new ObjectMapper().writeValueAsString(result);
    }
}
