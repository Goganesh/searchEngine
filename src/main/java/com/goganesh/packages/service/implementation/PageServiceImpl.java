package com.goganesh.packages.service.implementation;

import com.goganesh.packages.domain.*;
import com.goganesh.packages.exception.LemmaException;
import com.goganesh.packages.exception.StopActiveProcessException;
import com.goganesh.packages.repository.FieldRepository;
import com.goganesh.packages.repository.PageRepository;
import com.goganesh.packages.service.*;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;

@Service
@AllArgsConstructor
@Transactional
public class PageServiceImpl implements PageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PageServiceImpl.class);

    private final WebParser webParser;
    private final PageRepository pageRepository;
    private final LemmaService lemmaService;
    private final IndexService indexService;
    private final FieldRepository fieldRepository;
    private final ProcessService processService;

    @Override
    public Set<Page> parseAllPagesByUrl(String url) {
        Set<Page> checkedPages = Collections.newSetFromMap(new ConcurrentHashMap<>());//ConcurrentSkipListSet();
        Page page = parsePageByUrl(url);

        checkedPages.add(page);

        Set<Page> pages = new ForkJoinPool().invoke(new RecursivePageParser(
                checkedPages,
                page,
                this,
                webParser,
                processService)
        );

        LOGGER.info("URL " + url + " processed, unique links - " + checkedPages.size());
        return pages;
    }

    @Override
    public void dropIndexPage(Page page) {
        indexService.findByPage(page)
                .forEach(indexService::delete);
        pageRepository.delete(page);
    }

    @Override
    public Page parsePageByUrl(String url)  {
        return webParser.parsePageByUrl(url);
    }

    private String getTextByPageField(Page page, Field field) {
        return webParser.getTextByPageAndField(page, field.getSelector());
    }

    @Override
    public void indexPage(Page page) {
        if (!processService.isProcessActive(ProcessService.Type.INDEX)){
            LOGGER.debug("Index site was interrupted - page parsing " + page);
            throw new StopActiveProcessException("Index site was interrupted");
        }

        Map<String, Float> ratedLemmas;
        try {
            ratedLemmas = findRatedLemmasByPage(page);
        } catch (LemmaException | IOException e) {
            //TODO log
            return;
        }
        for (Map.Entry<String, Float> entry : ratedLemmas.entrySet()) {
            String lemma = entry.getKey();
            Float rate = entry.getValue();

            Index index = Index.builder()
                    .page(page)
                    .lemma(lemma)
                    .rank(rate)
                    .build();

            indexService.save(index);
        }
    }

    @Override
    public Map<String, Float> findRatedLemmasByPage(Page page) throws IOException {
        List<Field> fields = fieldRepository.findAll();

        Map<String, Float> ratedLemmas = new HashMap<>();

        for (Field field : fields) {
            String text = getTextByPageField(page, field);
            Map<String, Integer> lemmas = lemmaService.getLemmasCountByText(text);

            for(Map.Entry<String, Integer> entry : lemmas.entrySet()) {
                String key = entry.getKey();
                Float rate = entry.getValue() * field.getWeight();

                if (ratedLemmas.containsKey(key)) {
                    Float newRate = ratedLemmas.get(key) + rate;
                    ratedLemmas.put(key, newRate);
                } else {
                    ratedLemmas.put(key, rate);
                }
            }
        }

        return ratedLemmas;
    }

    @Override
    public List<Page> saveAll(Iterable<Page> pages) {
        return pageRepository.saveAll(pages);
    }

    @Override
    public List<Page> findBySite(Site site) {
        return pageRepository.findBySite(site);
    }

    @Override
    public long countPages(){
        return pageRepository.count();
    }

    @Override
    public Page save(Page page) {
        return pageRepository.save(page);
    }

    @Override
    public Optional<Page> findByPath(String path) {
        return pageRepository.findByPath(path);
    }
}
