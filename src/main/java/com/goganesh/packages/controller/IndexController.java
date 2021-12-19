package com.goganesh.packages.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.goganesh.packages.domain.Site;
import com.goganesh.packages.exception.*;
import com.goganesh.packages.service.*;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class IndexController {

    private static final Logger LOGGER = LoggerFactory.getLogger(IndexController.class);

    private static final String ACTIVE_PROCESS_ERROR = "Индексация уже запущена";
    private static final String INACTIVE_PROCESS_ERROR = "Индексация не запущена";
    private static final String EMPTY_SEARCH_QUERY_ERROR = "Задан пустой поисковый запрос";
    private static final String PAGE_OUT_OF_SITE_RANGE = "Данная страница находится за пределами сайтов, указанных в конфигурационном файле";

    private final ProcessService processService;
    private final SiteService siteService;

    @SneakyThrows
    @GetMapping(
            value = "/startIndexing",
            produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    public String startIndexing() {

        try {
            processService.blockProcess(ProcessService.Type.INDEX);
        } catch (ActiveProcessException e) {
            throw new ApiException(ACTIVE_PROCESS_ERROR, HttpStatus.OK);
        }

        new Thread(() -> {
            siteService.findAll()
                    .forEach(site -> {
                        if (site.getStatus().equals(Site.Status.INDEXED) || site.getStatus().equals(Site.Status.FAILED))
                            siteService.dropIndexPageBySite(site);
                        siteService.parseSite(site);
                        siteService.indexSite(site);
                    });

            processService.unblockProcess(ProcessService.Type.INDEX);
        }).start();


        Map<String, Object> result = new HashMap<>();
        result.put("result", true);

        return new ObjectMapper().writeValueAsString(result);
    }

    @SneakyThrows
    @GetMapping(
            value = "/stopIndexing",
            produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    public String stopIndexing() {

        if (processService.isProcessActive(ProcessService.Type.INDEX)) {
            processService.unblockProcess(ProcessService.Type.INDEX);
        } else {
            throw new ApiException(INACTIVE_PROCESS_ERROR, HttpStatus.OK);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("result", true);

        return new ObjectMapper().writeValueAsString(result);
    }

    @SneakyThrows
    @PostMapping(
            value = "/indexPage",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE}
    )
    public String indexPage(@RequestParam Map<String,String> param) {
        try {
            processService.blockProcess(ProcessService.Type.INDEX);
        } catch (ActiveProcessException e) {
            throw new ApiException(ACTIVE_PROCESS_ERROR, HttpStatus.OK);
        }

        if (!param.containsKey("url") || param.get("url").isBlank())
            throw new ApiException(EMPTY_SEARCH_QUERY_ERROR, HttpStatus.OK);

        try {
            siteService.indexSitePageByUrl(param.get("url"));
        } catch (NoSiteFoundException | BadBaseUrlException e) {
            LOGGER.error(e.getMessage());
            throw new ApiException(PAGE_OUT_OF_SITE_RANGE, HttpStatus.OK);
        } finally {
            processService.unblockProcess(ProcessService.Type.INDEX);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("result", true);

        return new ObjectMapper().writeValueAsString(result);
    }

}
