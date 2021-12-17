package com.goganesh.packages.service;

import com.goganesh.packages.domain.Page;
import com.goganesh.packages.domain.Site;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface PageService {

    Set<Page> parseAllPagesByUrl(String url) throws IOException;
    Page parsePageByUrl(String url);
    void indexPage(Page page);
    void dropIndexPage(Page page);
    Map<String, Float> findRatedLemmasByPage(Page page) throws IOException;

    List<Page> saveAll(Iterable<Page> pages);
    Page save(Page page);
    List<Page> findBySite(Site site);
    long countPages();
    Optional<Page> findByPath(String path);
}
