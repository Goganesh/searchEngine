package com.goganesh.packages.dto;

import lombok.Data;

@Data
public class PageDto {
    private String uri;
    private String title;
    private String snippet;
    private Float relevanceAbs;
    private Float relevanceRel;
}
