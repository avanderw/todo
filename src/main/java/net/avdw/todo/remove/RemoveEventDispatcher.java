package net.avdw.todo.remove;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.pmw.tinylog.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;

public class RemoveEventDispatcher implements RemoveApi{
    private EventBus eventBus;

    @Inject
    RemoveEventDispatcher(EventBus eventBus) {

        this.eventBus = eventBus;
    }

    public void remove(Integer idx) {
        eventBus.post(new RemoveEvent(Collections.singletonList(idx)));
    }

    public void remove(List<Integer> idxs) {
        eventBus.post(new RemoveEvent(idxs));
    }
}
