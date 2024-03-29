package com.goganesh.packages.service.implementation;

import com.goganesh.packages.domain.Page;
import com.goganesh.packages.exception.BadBaseUrlException;
import com.goganesh.packages.exception.StopActiveProcessException;
import com.goganesh.packages.service.PageService;
import com.goganesh.packages.service.ProcessService;
import com.goganesh.packages.service.WebParser;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Collectors;

@AllArgsConstructor
public class RecursivePageParser extends RecursiveTask<Set<Page>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(RecursivePageParser.class);

    private final Set<Page> checkedPages;
    private final Page page;
    private final PageService pageService;
    private final WebParser webParser;
    private final ProcessService processService;

    @SneakyThrows
    @Override
    protected Set<Page> compute() {
        if (!processService.isProcessActive(ProcessService.Type.INDEX)){
            LOGGER.debug("Index site was interrupted - page parsing " + page);
            throw new StopActiveProcessException("Index site was interrupted");
        }
        Set<String> currentLinks = checkedPages.stream()
                .map(Page::getPath)
                .collect(Collectors.toSet());

        Set<String> newLinks = webParser.getDomainUrlsByPage(page)
                .stream()
                .filter(link -> !currentLinks.contains(link))
                .collect(Collectors.toSet());

        List<RecursivePageParser> taskList = new ArrayList<>();

        for (String url : newLinks) {
            Page page;
            try {
                page = pageService.parsePageByUrl(url);
            } catch (BadBaseUrlException e) {
                LOGGER.debug("In main URL - " + this.page.getPath() + " , " + e.getMessage());
                continue;
            }
            checkedPages.add(page);

            RecursivePageParser task = new RecursivePageParser(
                    checkedPages,
                    page,
                    pageService,
                    webParser,
                    processService
            );
            task.fork();
            taskList.add(task);
        }

        for (RecursivePageParser task : taskList) {
            Set<Page> links = task.join();
            checkedPages.addAll(links);

            LOGGER.debug("URL " + page.getPath() + " processing, unique links - " + checkedPages.size());
        }

        return checkedPages;
    }

}
