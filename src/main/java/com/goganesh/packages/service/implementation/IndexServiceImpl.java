package com.goganesh.packages.service.implementation;

import com.goganesh.packages.domain.Index;
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
    public List<Index> findAll(){
        return indexRepository.findAll();
    }
}
