package com.goganesh.packages.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Detailed {
    private String url;
    private String name;
    private String status;
    private long statusTime;
    private String error;
    private long pages;
    private long lemmas;
}
