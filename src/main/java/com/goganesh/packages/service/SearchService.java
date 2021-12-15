package com.goganesh.packages.service;

import com.goganesh.packages.dto.SearchResult;

import java.util.List;

public interface SearchService {

    List<SearchResult> getSearchResult(String searchText);
}
