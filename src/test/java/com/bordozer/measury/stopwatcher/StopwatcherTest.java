package com.bordozer.measury.stopwatcher;

import com.bordozer.commons.utils.FileUtils;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SuppressWarnings("PMD.TooManyMethods")
class StopwatcherTest {

    private static final String EXPECTED_STOPWATCHER_REPORT1 = FileUtils.readSystemResource("tests/expected-stopwatcher-report-1.txt");
    private static final String EXPECTED_STOPWATCHER_REPORT2 = FileUtils.readSystemResource("tests/expected-stopwatcher-report-2.txt");
    @SuppressWarnings("PMD.UnusedPrivateField")
    private static final String EXPECTED_STOPWATCHER_MULTIPLE_THREAD_REPORT =
            FileUtils.readSystemResource("tests/expected-stopwatcher-multiple-thread-report.txt");

    private static final Stopwatcher SW1 = StopwatchManager.instance("KEY-1");
    private static final Stopwatcher SW2 = StopwatchManager.instance("KEY-2");
    private static final Stopwatcher SW_MT = StopwatchManager.instance("MULTIPLE-THREAD");

    // Disabled due to milliseconds
    @Disabled
    @Test
    void shouldCreateReport() {
        // given

        // when
        SW1.measure("Main Flow", this::fakeActivity);

        // then
        final String report1 = SW1.buildReportMills();
        assertThat(report1).isNotNull();
        assertThat(report1).isEqualTo(EXPECTED_STOPWATCHER_REPORT1);

        final String report2 = SW2.buildReportSecs();
        assertThat(report2).isNotNull();
        assertThat(report2).isEqualTo(EXPECTED_STOPWATCHER_REPORT2);
    }

    @Test
    void shouldCreateMultithreadedReport() {
        // given

        // when
        SW_MT.measure("Multiple thread flow", this::executeFutures);
        SW_MT.measure("Multiple thread flow", this::executeCallable);

        // then
        final String report = SW_MT.buildReportSecs();
        assertThat(report).isNotNull();
        SW_MT.logReportSecs();
//        assertThat(report).isEqualTo(EXPECTED_STOPWATCHER_MULTIPLE_THREAD_REPORT);
    }

    @Test
    void shouldStartStopwatcher() {
        // given
        final Stopwatcher stopwatcher = new Stopwatcher("BBB");

        // when

        // then
        assertThat(stopwatcher.getCheckpoints()).isNotNull().hasSize(0);
        assertThat(stopwatcher.getCurrentIndent()).isEqualTo(0);
    }

    private void fakeActivity() {
        SW1.measure("Level a", this::rootStep);
        SW1.measure("Level b", this::loopSteps);
    }

    private void rootStep() {
        SW2.measure("Measure 2", () -> {
            SW1.measure("Level a.1", () -> waifFor(1000));
            waifFor(1000);
            SW1.measure("Level a.2", () -> waifFor(3000));
        });
    }

    private void loopSteps() {
        IntStream.of(1000, 2000, 4000)
                .forEach(interval -> SW1.measure("Level b.1", () -> waifFor(interval)));
        waifFor(1000);
        SW1.measure("Level b.1", () -> waifFor(3000));
    }

    @SneakyThrows
    private void executeCallable() {
        waifFor(1000);
        final List<Callable<String>> futures = IntStream.of(1, 2)
                .mapToObj(number -> Executors.callable(() -> doTask(number), "Lalala"))
                .collect(Collectors.toList());
        final ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        executorService.invokeAll(futures);
    }

    private void executeFutures() {
        waifFor(1000);
        final List<CompletableFuture<String>> futures = IntStream.of(1, 2, 3, 4)
                .mapToObj(this::createFuture)
                .collect(Collectors.toList());
        final List<String> results = SW_MT.measureAndReturn("Calling the futures", () -> completeFutures(futures));
        log.info("Futures' results: {}", results);
    }

    @SuppressFBWarnings("REC_CATCH_EXCEPTION")
    private List<String> completeFutures(final List<CompletableFuture<String>> futures) {
        SW_MT.measure("allOf", () -> CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join());
        return futures.stream()
                .map(future -> {
                    try {
                        return future.get();
                    } catch (final InterruptedException | ExecutionException ex) {
                        return "ERROR";
                    }
                })
                .collect(Collectors.toList());
    }

    private CompletableFuture<String> createFuture(final int number) {
        return CompletableFuture.supplyAsync(() -> doTask(number));
    }

    private String doTask(final int number) {
        return SW_MT.measureAndReturn("Task is about to executing", () -> {
            waifFor(3000);
            return String.format("Task #%s completed", number);
        });
    }

    @SneakyThrows
    private static void waifFor(final int millis) {
        Thread.sleep(millis);
    }
}
