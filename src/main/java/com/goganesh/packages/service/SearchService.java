package com.goganesh.packages.service;

import com.goganesh.packages.dto.PageDto;

import java.util.List;

public interface SearchService {

    List<PageDto> findPagesDtoBySearchText(String searchText);
}
