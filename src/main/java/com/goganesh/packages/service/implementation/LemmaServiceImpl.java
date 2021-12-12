package com.goganesh.packages.service.implementation;

import com.goganesh.packages.domain.Index;
import com.goganesh.packages.exception.LemmaException;
import com.goganesh.packages.exception.NoIndexFoundException;
import com.goganesh.packages.service.IndexService;
import com.goganesh.packages.service.LemmaService;
import lombok.AllArgsConstructor;
import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class LemmaServiceImpl implements LemmaService {

    private static final String PREDLOG = "ПРЕДЛ";
    private static final String SOUZ = "СОЮЗ";
    private static final String MEZDOMETIE = "МЕЖД";

    private final IndexService indexService;

    @Override
    public Set<String> getLemmasByText(String text) throws IOException {
        return getLemmasCountByText(text).keySet();
    }

    @Override
    public Map<String, Integer> getLemmasFrequency(Set<String> lemmas) {
        Map<String, Integer> lemmasFrequency = new HashMap<>();

        lemmas.stream()
                .map(indexService::findByLemma)
                //.filter() //exception
                .flatMap(List::stream)
                .forEach(index -> lemmasFrequency.merge(index.getLemma(), 1 , Integer::sum));

        return lemmasFrequency;
    }

    @Override
    public Map<String, Integer> getLemmasCountByText(String text) throws IOException {
        Map<String, Integer> lemmas = new HashMap<>();
        LuceneMorphology luceneMorph = new RussianLuceneMorphology();

        List<String> words = getClearWordsByText(text);

        for (String word : words) {
            List<String> wordBaseForms;
            try {
                wordBaseForms = luceneMorph.getMorphInfo(word);
            } catch (Exception ex) {
                throw new LemmaException("Text is null");
            }

            for (String lemmaInfo : wordBaseForms) {
                String lemma = lemmaInfo.split("[|]")[0];
                String lemmaType = lemmaInfo.split("[|]")[1].split(" ")[1];

                if (lemmaType.equals(PREDLOG) || lemmaType.equals(SOUZ) || lemmaType.equals(MEZDOMETIE))
                    continue;

                lemmas.merge(lemma, 1, Integer::sum);
            }
        }

        return lemmas;
    }

    private List<String> getClearWordsByText(String text) throws IOException {
        LuceneMorphology luceneMorph = new RussianLuceneMorphology();

        return Arrays.stream(text.toLowerCase()
                        .replaceAll("[,.!?:]", "")
                        .replaceAll("[\n]", " ")
                        .split(" "))
                .filter(luceneMorph::checkString)
                .collect(Collectors.toList());
    }

}
