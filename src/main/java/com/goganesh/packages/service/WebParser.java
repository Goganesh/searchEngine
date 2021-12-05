package com.goganesh.packages.service;

import com.goganesh.packages.domain.Page;

import java.io.IOException;
import java.util.Set;

public interface WebParser {
    Set<String> getDomainUrlsByPage(Page page);
    Page parsePageByUrl(String url) throws IOException;
}
