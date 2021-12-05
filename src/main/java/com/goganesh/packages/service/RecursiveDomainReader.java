package com.goganesh.packages.service;

import com.goganesh.packages.domain.Page;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private static final int DELAY = 250;

    private final Set<Page> checkedPages;
    private final Page page;
    private final PageService pageService;
    private final WebParser webParser;

    @SneakyThrows
    @Override
    protected Set<Page> compute() {
        Set<String> currentLinks = checkedPages.stream()
                .map(page -> page.getPath())
                .collect(Collectors.toSet());

        Set<String> newLinks = webParser.getDomainUrlsByPage(page)
                .stream()
                .filter(link -> !currentLinks.contains(link))
                .collect(Collectors.toSet());

        List<RecursiveDomainReader> taskList = new ArrayList<>();

        for (String url : newLinks) {
            Page page;
            try {
                Thread.sleep(DELAY);
                page = pageService.parsePageByUrl(url);
            } catch (IOException e) {
                LOGGER.error("In main URL - " + this.page.getPath() + " , Bad sub URL parsing - " + url);
                continue;
            }
            checkedPages.add(page);

            RecursiveDomainReader task = new RecursiveDomainReader(checkedPages, page, pageService, webParser);
            task.fork();
            taskList.add(task);
        }

        for (RecursiveDomainReader task : taskList) {
            Set<Page> links = task.join();
            for (Page link : links) {
                checkedPages.add(link);
            }

            LOGGER.info("Host " + new URL(page.getPath()).getHost() + " processing, unique links - " + checkedPages.size());
        }

        return checkedPages;
    }

}
