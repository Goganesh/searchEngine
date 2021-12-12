package com.goganesh.packages.service;

import com.goganesh.packages.domain.Site;

import java.util.List;
import java.util.UUID;

public interface SiteService {

    void parseSite(Site site);
    void indexSite(Site site);
    void dropIndexSite(Site site);

    Site save(Site site);
    List<Site> findAll();
}
