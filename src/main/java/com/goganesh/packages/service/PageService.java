package com.goganesh.packages.service;

import com.goganesh.packages.domain.Page;
import com.goganesh.packages.domain.Site;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface PageService {

    Set<Page> parseAllPagesByUrl(String url) throws IOException;
    Page parsePageByUrl(String url);
    void indexPage(Page page);
    void dropIndexPage(Page page);
    Map<String, Float> findRatedLemmasByPage(Page page) throws IOException;

    List<Page> saveAll(Iterable<Page> pages);
    List<Page> findBySite(Site site);

}
