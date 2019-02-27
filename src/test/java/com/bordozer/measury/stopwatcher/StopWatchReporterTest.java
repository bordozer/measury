package com.bordozer.measury.stopwatcher;

import com.bordozer.commons.utils.FileUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(Lifecycle.PER_CLASS)
class StopWatchReporterTest {

    private static final String EXPECTED_STOPWATCHER_MILLS_REPORT = FileUtils.readSystemResource("tests/expected-stopwatcher-mills-report.txt");
    private static final String EXPECTED_STOPWATCHER_SECS_REPORT = FileUtils.readSystemResource("tests/expected-stopwatcher-secs-report.txt");

    @BeforeAll
    static void setUp() {
        Thread.currentThread().setName("main");
    }

    @Test
    void shouldBuildMillsReport() {
        // given
        final List<CheckPoint> checkPoints = createCheckPointsMills();

        // when
        final String report = StopWatchReporter.buildReportMills("A MILLS", checkPoints);

        // then
        assertThat(report).isNotNull();
        assertThat(report).isEqualTo(EXPECTED_STOPWATCHER_MILLS_REPORT);
    }

    @Test
    void shouldBuildSecsReport() {
        // given
        final List<CheckPoint> checkPoints = createCheckPointsSecs();

        // when
        final String report = StopWatchReporter.buildReportSecs("SECONDS", checkPoints);

        // then
        assertThat(report).isNotNull();
        assertThat(report).isEqualTo(EXPECTED_STOPWATCHER_SECS_REPORT);
    }

    private List<CheckPoint> createCheckPointsMills() {
        final CheckPoint point1 = new CheckPoint("Point1", 0);
        point1.addExecutions(0, 4000123);

        final CheckPoint point2 = new CheckPoint("Point2", 1);
        point2.addExecutions(0, 3000000);
        point2.addExecutions(6000000, 6000005);
        point2.setInvocationCount(12345);

        final CheckPoint point3 = new CheckPoint("Point3", 2);
        point3.addExecutions(0, 111);
        point3.setInvocationCount(777);

        final CheckPoint point4 = new CheckPoint("Point4", 0);
        point4.addExecutions(0, 500000);
        point4.addExecutions(200, 750);
        point4.setInvocationCount(10);

        final CheckPoint point5 = new CheckPoint("Point4", 0);
        point5.addExecutions(0, 1);
        point5.setInvocationCount(1234567890);

        return newArrayList(point1, point2, point3, point4, point5);
    }

    private List<CheckPoint> createCheckPointsSecs() {
        final CheckPoint point1 = new CheckPoint("Point1", 0);
        point1.addExecutions(0, 4000000);

        final CheckPoint point2 = new CheckPoint("Point2", 1);
        point2.addExecutions(0, 3000000);
        point2.addExecutions(6000000, 6005000);
        point2.setInvocationCount(12345);

        final CheckPoint point3 = new CheckPoint("Point3", 2);
        point3.addExecutions(0, 1000);
        point3.setInvocationCount(777);

        final CheckPoint point4 = new CheckPoint("Point4", 0);
        point4.addExecutions(0, 999);
        point4.addExecutions(200, 1200);
        point4.setInvocationCount(10);

        final CheckPoint point5 = new CheckPoint("Point4", 0);
        point5.addExecutions(0, 10000);
        point5.setInvocationCount(1234567890);

        return newArrayList(point1, point2, point3, point4, point5);
    }
}
