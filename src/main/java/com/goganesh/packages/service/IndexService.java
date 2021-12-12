package com.goganesh.packages.service;

import com.goganesh.packages.domain.Index;
import com.goganesh.packages.domain.Page;

import java.util.List;

public interface IndexService {

    Index save(Index index);
    List<Index> findByLemma(String lemma);
    List<Index> findByPage(Page page);
    void delete(Index index);
}
