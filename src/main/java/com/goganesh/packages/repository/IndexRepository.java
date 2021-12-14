package com.goganesh.packages.repository;

import com.goganesh.packages.domain.Index;
import com.goganesh.packages.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface IndexRepository extends JpaRepository<Index, UUID> {

    List<Index> findByLemma(String lemma);
    List<Index> findByPage(Page page);

    @Query("select count(distinct l.lemma) from Index l")
    long countLemmas();
}
