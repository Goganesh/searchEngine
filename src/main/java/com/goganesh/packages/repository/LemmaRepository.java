package com.goganesh.packages.repository;

import com.goganesh.packages.domain.Lemma;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LemmaRepository extends JpaRepository<Lemma, UUID> {

    Lemma findByLemma(String lemma);
}
