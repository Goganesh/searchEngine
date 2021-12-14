package com.goganesh.packages.service.implementation;

import com.goganesh.packages.exception.ActiveProcessException;
import com.goganesh.packages.service.ProcessService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ProcessServiceImpl implements ProcessService {

    public static Map<Type, Boolean> processState = new HashMap<>() {{
        put(Type.INDEX, false);
    }};

    @Override
    public void blockProcess(Type type) {
        if (processState.get(type))
            throw new ActiveProcessException("Process " + type.name() + " can not be block, because it is blocked");
        processState.put(type, true);
    }

    @Override
    public void unblockProcess(Type type) {
        processState.put(type, false);
    }

    @Override
    public boolean isProcessActive(Type type) {
        return processState.get(type);
    }
}
