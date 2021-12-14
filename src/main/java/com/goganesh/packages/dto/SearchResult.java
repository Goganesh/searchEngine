package com.goganesh.packages.dto;

import lombok.Data;

@Data
public class SearchResult {
    private String uri;
    private String title;
    private String snippet;
    private Float relevanceAbs;
    private Float relevanceRel;
}
