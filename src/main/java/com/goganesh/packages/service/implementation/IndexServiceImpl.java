package com.goganesh.packages.service.implementation;

import com.goganesh.packages.domain.Index;
import com.goganesh.packages.domain.Page;
import com.goganesh.packages.exception.NoIndexFoundException;
import com.goganesh.packages.repository.IndexRepository;
import com.goganesh.packages.service.IndexService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class IndexServiceImpl implements IndexService {

    private final IndexRepository indexRepository;

    @Override
    public Index save(Index index) {
        return indexRepository.save(index);
    }

    @Override
    public List<Index> findByPage(Page page) {
        return indexRepository.findByPage(page);
    }

    @Override
    public void delete(Index index) {
        indexRepository.delete(index);
    }

    @Override
    public List<Index> findByLemma(String lemma) {
        List<Index> indexes = indexRepository.findByLemma(lemma);
        if (indexes.isEmpty())
            throw new NoIndexFoundException("No such index found by lemma " + lemma);
        return indexes;
    }
}
