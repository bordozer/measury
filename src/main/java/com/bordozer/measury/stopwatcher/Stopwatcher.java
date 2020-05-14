package com.bordozer.measury.stopwatcher;

import com.google.common.base.Stopwatch;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.CheckForNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Getter
@Setter
public final class Stopwatcher {

    private final String key;
    @CheckForNull
    private String reportName;

    private final List<CheckPoint> checkpoints = new ArrayList<>();
    private int currentIndent;

    Stopwatcher(final String key) {
        this.key = key;
    }

    public Stopwatcher measure(final String name, final MeasurableVoid func) {
        measureAndReturn(name, () -> {
            func.execute();
            return Void.TYPE;
        });
        return this;
    }

    public <T> T measureAndReturn(final String name, final MeasurableFunc<T> func) {
        LOGGER.debug("Stopwatcher '{}' started measuring '{}' [{}]", key, name, getCurrentThreadName());
        final Stopwatch stopwatch = Stopwatch.createStarted();

        final long startedAt = stopwatch.elapsed(TimeUnit.MILLISECONDS);
        final CheckPoint checkPoint = findOrCreateCheckPoint(name);

        final T result = func.executeAndReturn();

        final long finishedAt = stopwatch.elapsed(TimeUnit.MILLISECONDS);
        checkPoint.addExecutions(startedAt, finishedAt);

        currentIndent--;

        LOGGER.debug("Stopwatcher '{}' finished measuring '{}' [{}]", key, name, getCurrentThreadName());

        return result;
    }

    public void reset() {
        synchronized (checkpoints) {
            checkpoints.clear();
            currentIndent = 0;
        }
    }

    public List<CheckPoint> getCheckpoints() {
        return checkpoints;
    }

    public String buildReportMills() {
        return StopWatchReporter.buildReportMills(key, reportName, checkpoints);
    }

    public String buildReportSecs() {
        return StopWatchReporter.buildReportSecs(key, reportName, checkpoints);
    }

    public void logReportMills() {
        LOGGER.debug(buildReportMills());
    }

    public void logReportSecs() {
        LOGGER.debug(buildReportSecs());
    }

    private CheckPoint findOrCreateCheckPoint(final String name) {
        return getCheckpointOfCurrentThreadOptional(name).orElseGet(() -> {
            synchronized (checkpoints) {
                return getCheckpointOfCurrentThreadOptional(name).orElseGet(() -> addNewCheckPoint(name));
            }
        });
    }

    private CheckPoint addNewCheckPoint(final String name) {
        final CheckPoint point = new CheckPoint(name, currentIndent);
        checkpoints.add(point);
        currentIndent++;
        return point;
    }

    private Optional<CheckPoint> getCheckpointOfCurrentThreadOptional(final String name) {
        return checkpoints.stream()
                .filter(point -> point.getName().equals(name) && point.getThread().equals(getCurrentThreadName()))
                .findFirst();
    }

    private String getCurrentThreadName() {
        return Thread.currentThread().getName();
    }
}
