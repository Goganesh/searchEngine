package com.goganesh.packages.service;

import com.goganesh.packages.domain.Page;
import com.goganesh.packages.exception.BadBaseUrlException;
import com.goganesh.packages.exception.NoPageFoundException;
import com.goganesh.packages.repository.PageRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ForkJoinPool;

@Service
@AllArgsConstructor
@Transactional
public class PageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PageService.class);

    private final WebParser webParser;
    private final PageRepository pageRepository;

    public Set<Page> parseAllPagesByUrl(String url) throws IOException {
        Set<Page> checkedPages = new ConcurrentSkipListSet();
        Page page;
        try {
            page = parsePageByUrl(url);
        } catch (IOException ioException) {
            throw new BadBaseUrlException("Bad main URL parsing - " + url);
        }
        checkedPages.add(page);

        Set<Page> pages = new ForkJoinPool().invoke(new RecursiveDomainReader(checkedPages,
                page,
                this,
                webParser));

        LOGGER.info("Host " + new URL(page.getPath()).getHost() + " processed, unique links - " + checkedPages.size());

        return pages;
    }

    public Page parsePageByUrl(String url) throws IOException {
        return webParser.parsePageByUrl(url);
    }

    public List<Page> saveAll(Iterable<Page> pages) {
        return pageRepository.saveAll(pages);
    }

    public List<Page> findAll() {
        return pageRepository.findAll();
    }

    public Page findById(UUID id) {
        return pageRepository.findById(id)
                .orElseThrow(() -> new NoPageFoundException("No page found with id - " + id));
    }
}
