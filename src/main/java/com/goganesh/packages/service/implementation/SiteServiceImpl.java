package com.goganesh.packages.service.implementation;

import com.goganesh.packages.domain.Page;
import com.goganesh.packages.domain.Site;
import com.goganesh.packages.exception.BadBaseUrlException;
import com.goganesh.packages.exception.IndexErrorException;
import com.goganesh.packages.exception.NoSiteFoundException;
import com.goganesh.packages.repository.SiteRepository;
import com.goganesh.packages.service.PageService;
import com.goganesh.packages.service.SiteService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class SiteServiceImpl implements SiteService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SiteServiceImpl.class);

    private final SiteRepository siteRepository;
    private final PageService pageService;

    @Override
    public void indexSite(Site site) {
        LOGGER.info("Start index site - " + site);

        List<Page> pagesForIndex;
        try {
            pagesForIndex = getPagesForIndexBySite(site);
            pagesForIndex.forEach(pageService::indexPage);
            site.setStatus(Site.Status.INDEXED);
            site.setStatusTime(LocalDateTime.now());
            save(site);
        } catch (IndexErrorException e) {
            LOGGER.error(e.getMessage());
            return;
        } finally {
            LOGGER.info("Finish index site - " + site);
        }
    }

    @Override
    public void parseSite(Site site) {
        LOGGER.info("Start parse site - " + site);

        Set<Page> checkedPages;
        Site.Status status = Site.Status.PARSED;
        String error = "";

        try {
            checkedPages = pageService.parseAllPagesByUrl(site.getUrl());
            checkedPages.forEach(page -> page.setSite(site));
            pageService.saveAll(checkedPages);
        } catch (BadBaseUrlException | IOException e) {
            status = Site.Status.FAILED;
            error = e.getMessage();
            LOGGER.error(error);
            return;
        } finally {
            site.setStatus(status);
            site.setStatusTime(LocalDateTime.now());
            site.setLastError(error);
            siteRepository.save(site);
            LOGGER.info("Finish parse site - " + site);
        }
    }

    @Override
    public Site findById(UUID id) {
        return siteRepository.findById(id)
                .orElseThrow(() -> new NoSiteFoundException("No site found with id - " + id));
    }

    @Override
    public List<Site> findAll() {
        return siteRepository.findAll();
    }

    @Override
    public Site save(Site site) {
        return siteRepository.save(site);
    }

    @Override
    public List<Site> saveAll(Iterable<Site> sites) {
        return siteRepository.saveAll(sites);
    }

    private List<Page> getPagesForIndexBySite(Site site) {
        if (!site.getStatus().equals(Site.Status.PARSED)) {
            throw new IndexErrorException("Site has bad status for index " + site);
        }

        return pageService.findBySite(site)
                .stream()
                .filter(page -> page.getCode() == 200)
                .collect(Collectors.toList());
    }
}
