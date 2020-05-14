package com.bordozer.measury.stopwatcher;

import org.apache.commons.lang3.StringUtils;

import javax.annotation.CheckForNull;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.StampedLock;

public final class StopwatchManager {

    private static final String SINGLETON = "";
    private static final List<Stopwatcher> WATCHERS = new ArrayList<>();

    private StopwatchManager() {
    }

    public static Stopwatcher singleton() {
        return instance(SINGLETON);
    }

    public static Stopwatcher instance(final String key) {
        checkNotBlank(key);

        final StampedLock lock = new StampedLock();

        long stamp = lock.readLock();
        try {
            @CheckForNull final Stopwatcher found = WATCHERS.stream()
                    .filter(watcher -> watcher.getKey().equals(key))
                    .findFirst()
                    .orElse(null);
            if (found != null) {
                return found;
            }
            stamp = lock.tryConvertToWriteLock(stamp);
            if (stamp == 0L) {
                stamp = lock.writeLock();
            }
            final Stopwatcher stopwatcher = new Stopwatcher(key);
            WATCHERS.add(stopwatcher);
            return stopwatcher;
        } finally {
            lock.unlock(stamp);
        }
    }

    private static void checkNotBlank(final String key) {
        if (StringUtils.isBlank(key)) {
            throw new IllegalArgumentException("Watcher key cannot be blank");
        }
    }
}
