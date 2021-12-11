package com.goganesh.packages.service;

import com.goganesh.packages.domain.Lemma;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

public interface LemmaService {

    Map<String, Integer> getLemmasCountByText(String text) throws IOException;
    Set<String> getLemmasByText(String text) throws IOException;

    Lemma findByLemma(String lemma);
    Lemma save(Lemma lemma);

}
