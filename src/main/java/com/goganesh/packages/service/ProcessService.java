package com.goganesh.packages.service;

public interface ProcessService {

    void blockProcess(Type type);
    void unblockProcess(Type type);
    boolean isProcessActive(Type type);

    enum Type {
        INDEX
    }
}
