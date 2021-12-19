package com.goganesh.packages.service;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

public interface LemmaService {

    Map<String, Integer> getLemmasCountByText(String text) throws IOException;
    Set<String> getLemmasByText(String text) throws IOException;
    Map<String, Integer> getLemmasFrequency(Set<String> lemmas);

    long countLemmas();
}
