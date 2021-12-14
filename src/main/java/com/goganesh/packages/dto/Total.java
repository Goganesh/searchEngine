package com.goganesh.packages.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Total {
    private long sites;
    private long pages;
    private long lemmas;
    private boolean isIndexing;
}
