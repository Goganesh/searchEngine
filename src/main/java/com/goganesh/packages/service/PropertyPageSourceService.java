package com.goganesh.packages.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PropertyPageSourceService implements PageSourceService{
    @Value("${pagessourse}")
    private String[] pagesSource;

    @Override
    public List<String> getPagesSource() {
        return Arrays.stream(pagesSource).collect(Collectors.toList());
    }
}
