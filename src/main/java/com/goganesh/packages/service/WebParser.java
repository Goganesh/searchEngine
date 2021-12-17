package com.goganesh.packages.service;

import com.goganesh.packages.domain.Page;

import java.util.Set;

public interface WebParser {
    Set<String> getDomainUrlsByPage(Page page);
    Page parsePageByUrl(String url);
    String getTextByPageAndField(Page page, String tag);
}
