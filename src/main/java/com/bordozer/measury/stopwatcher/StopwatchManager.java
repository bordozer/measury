package com.bordozer.measury.stopwatcher;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public final class StopwatchManager {

    private static final List<Stopwatcher> STOPWATCHERS = new ArrayList<>();

    private StopwatchManager() {
    }

    public static Stopwatcher getInstance(final String key) {
        synchronized (StopwatchManager.class) {
            return STOPWATCHERS.stream()
                    .filter(watcher -> watcher.getKey().equals(key))
                    .findFirst()
                    .orElseGet(() -> createInstance(key));
        }
    }

    private static Stopwatcher createInstance(final String key) {
        final Stopwatcher stopwatcher = new Stopwatcher(key);
        STOPWATCHERS.add(stopwatcher);
        LOGGER.debug("Stopwatcher for key {} has been created", key);
        return stopwatcher;
    }
}
