package com.goganesh.packages.service.implementation;

import com.goganesh.packages.domain.Page;
import com.goganesh.packages.exception.BadBaseUrlException;
import com.goganesh.packages.service.PageService;
import com.goganesh.packages.service.WebParser;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Collectors;

@AllArgsConstructor
public class RecursiveDomainReader extends RecursiveTask<Set<Page>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(RecursiveDomainReader.class);

    private final Set<Page> checkedPages;
    private final Page page;
    private final PageService pageService;
    private final WebParser webParser;

    @SneakyThrows
    @Override
    protected Set<Page> compute() {
        Set<String> currentLinks = checkedPages.stream()
                .map(Page::getPath)
                .collect(Collectors.toSet());

        Set<String> newLinks = webParser.getDomainUrlsByPage(page)
                .stream()
                .filter(link -> !currentLinks.contains(link))
                .collect(Collectors.toSet());

        List<RecursiveDomainReader> taskList = new ArrayList<>();

        for (String url : newLinks) {
            Page page;
            try {
                page = pageService.parsePageByUrl(url);
            } catch (BadBaseUrlException e) {
                LOGGER.debug("In main URL - " + this.page.getPath() + " , " + e.getMessage());
                continue;
            }
            checkedPages.add(page);

            RecursiveDomainReader task = new RecursiveDomainReader(checkedPages, page, pageService, webParser);
            task.fork();
            taskList.add(task);
        }

        for (RecursiveDomainReader task : taskList) {
            Set<Page> links = task.join();
            checkedPages.addAll(links);

            LOGGER.debug("URL " + page.getPath() + " processing, unique links - " + checkedPages.size());
        }

        return checkedPages;
    }

}
