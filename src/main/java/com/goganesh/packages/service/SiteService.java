package com.goganesh.packages.service;

import com.goganesh.packages.domain.Site;

import java.util.List;
import java.util.Optional;

public interface SiteService {

    void parseSite(Site site);
    void indexSite(Site site);
    void dropIndexPageBySite(Site site);
    void indexSitePageByUrl(String url);

    Site save(Site site);
    List<Site> findAll();
    Optional<Site> findByUrl(String url);
    long countSites();
}
