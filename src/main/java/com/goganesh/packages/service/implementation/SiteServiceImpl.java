package com.goganesh.packages.service.implementation;

import com.goganesh.packages.domain.Page;
import com.goganesh.packages.domain.Site;
import com.goganesh.packages.exception.BadBaseUrlException;
import com.goganesh.packages.exception.IndexErrorException;
import com.goganesh.packages.exception.NoSiteFoundException;
import com.goganesh.packages.exception.StopActiveProcessException;
import com.goganesh.packages.repository.SiteRepository;
import com.goganesh.packages.service.PageService;
import com.goganesh.packages.service.SiteService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class SiteServiceImpl implements SiteService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SiteServiceImpl.class);

    private final SiteRepository siteRepository;
    private final PageService pageService;

    @Override
    public void indexSitePageByUrl(String url) {
        URL pageUrl;
        try {
            pageUrl = new URL(url);
        } catch (MalformedURLException e) {
            throw new BadBaseUrlException("Bad URL parsing - " + url);
        }
        String host = pageUrl.getProtocol() + "://" + pageUrl.getHost();
        Site site = siteRepository.findByHost(host)
                .orElseThrow(() -> new NoSiteFoundException("Cannot find site by url " + url));

        pageService.findByPath(url).ifPresent(pageService::dropIndexPage);

        Page page = pageService.parsePageByUrl(url);
        page.setSite(site);
        page = pageService.save(page);
        pageService.indexPage(page);
    }

    @Override
    public void dropIndexPageBySite(Site site) {
        LOGGER.info("Drop index and pages for site - " + site);
        pageService.findBySite(site)
                .forEach(pageService::dropIndexPage);

        site.setStatus(Site.Status.NEW);
        site.setStatusTime(LocalDateTime.now());
        site.setLastError("");
        site.setPages(null);

        siteRepository.save(site);

        LOGGER.info("Drop index and pages for site - " + site);
    }

    @Override
    public void indexSite(Site site) {
        LOGGER.info("Start index site - " + site);

        List<Page> pagesForIndex;
        Site.Status status = Site.Status.INDEXED;
        String error = "";

        try {
            pagesForIndex = getPagesForIndexBySite(site);
            pagesForIndex.forEach(pageService::indexPage);
        } catch (IndexErrorException | StopActiveProcessException e) {
            status = Site.Status.FAILED;
            error = e.getMessage();
            LOGGER.error(e.getMessage());
        } finally {
            site.setStatus(status);
            site.setStatusTime(LocalDateTime.now());
            site.setLastError(error);
            siteRepository.save(site);
            LOGGER.info("Finish index site - " + site);
        }
    }

    @Override
    public void parseSite(Site site) {
        LOGGER.info("Start parse site - " + site);

        Set<Page> checkedPages;
        Site.Status status = Site.Status.INDEXING;
        String error = "";

        try {
            checkedPages = pageService.parseAllPagesByUrl(site.getUrl());
            checkedPages.forEach(page -> page.setSite(site));
            pageService.saveAll(checkedPages);
        } catch (BadBaseUrlException | IOException |StopActiveProcessException e) {
            status = Site.Status.FAILED;
            error = e.getMessage();
            LOGGER.error(error);
        } finally {
            site.setStatus(status);
            site.setStatusTime(LocalDateTime.now());
            site.setLastError(error);
            siteRepository.save(site);
            LOGGER.info("Finish parse site - " + site);
        }
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
    public Optional<Site> findByUrl(String url) {
        return siteRepository.findByUrl(url);
    }

    private List<Page> getPagesForIndexBySite(Site site) {
        if (!site.getStatus().equals(Site.Status.INDEXING)) {
            throw new IndexErrorException("Site has bad status for index " + site);
        }

        return pageService.findBySite(site)
                .stream()
                .filter(page -> page.getCode() == 200)
                .collect(Collectors.toList());
    }

    @Override
    public long countSites() {
        return siteRepository.count();
    }
}
