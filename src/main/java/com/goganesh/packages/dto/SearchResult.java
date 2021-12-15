package com.goganesh.packages.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SearchResult {
    private String uri;
    private String title;
    private String site;
    private String siteName;
    private String snippet;
    private Float relevance;
}
