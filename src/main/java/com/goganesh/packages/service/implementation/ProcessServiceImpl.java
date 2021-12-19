package com.goganesh.packages.service.implementation;

import com.goganesh.packages.exception.ActiveProcessException;
import com.goganesh.packages.service.ProcessService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ProcessServiceImpl implements ProcessService {

    private static final Map<Type, Boolean> PROCESS_STATE = new HashMap<>() {{
        put(Type.INDEX, false);
    }};

    @Override
    public void blockProcess(Type type) {
        if (PROCESS_STATE.get(type))
            throw new ActiveProcessException("Process " + type.name() + " can not be block, because it is blocked");
        PROCESS_STATE.put(type, true);
    }

    @Override
    public void unblockProcess(Type type) {
        if (!PROCESS_STATE.get(type))
            throw new ActiveProcessException("Process " + type.name() + " can not be unblock, because it is not active");
        PROCESS_STATE.put(type, false);
    }

    @Override
    public boolean isProcessActive(Type type) {
        return PROCESS_STATE.get(type);
    }
}
