package com.bordozer.measury.stopwatcher;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Getter
@Setter
public class CheckPoint {
    private final String name;
    private final int indent;
    private final List<Execution> executions = new ArrayList<>();
    private final String thread = Thread.currentThread().getName();
    private long invocationCount;

    /**
     * @param startedAt  - milliseconds;
     * @param finishedAt - milliseconds;
     **/
    public void addExecutions(final long startedAt, final long finishedAt) {
        synchronized (executions) {
            executions.add(new Execution(startedAt, finishedAt));
            invocationCount++;
        }
    }

    @RequiredArgsConstructor
    @Getter
    public static class Execution {
        private final long startedAt;
        private final long finishedAt;
    }
}
