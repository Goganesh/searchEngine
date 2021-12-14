package com.goganesh.packages.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class Statistics {
    private Total total;
    private List<Detailed> detailed;
}
