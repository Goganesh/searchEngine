package com.goganesh.packages.service;

import com.goganesh.packages.domain.Index;

import java.util.List;

public interface IndexService {

    Index save(Index index);
    List<Index> findAll();
}
