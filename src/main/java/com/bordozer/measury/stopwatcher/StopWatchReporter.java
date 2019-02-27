package com.bordozer.measury.stopwatcher;

import com.google.common.base.Strings;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;

@Slf4j
final class StopWatchReporter {

    private static final DateTimeFormatter REPORT_FILE_NAME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS");

    private static final int REPORT_COLUMN_NAME_LENGTH = 100;
    private static final int REPORT_COLUMN_DURATION_LENGTH = 12;
    private static final int REPORT_COLUMN_COUNT_LENGTH = 10;

    private StopWatchReporter() {
    }

    static String buildReportMills(final String key, final List<CheckPoint> checkPoints) {
        return buildReport(key, checkPoints, ChronoUnit.MILLIS);
    }

    static String buildReportSecs(final String key, final List<CheckPoint> checkPoints) {
        return buildReport(key, checkPoints, ChronoUnit.SECONDS);
    }

    private static String buildReport(final String key, final List<CheckPoint> checkPoints, final ChronoUnit chronoUnit) {
        final String headerTitle = String.format("Stopwatcher report for a key '%s'", key);

        final String headerSeparator = String.format("+ %s + %s + %s +",
                StringUtils.repeat("-", REPORT_COLUMN_NAME_LENGTH),
                StringUtils.repeat("-", REPORT_COLUMN_DURATION_LENGTH),
                StringUtils.repeat("-", REPORT_COLUMN_COUNT_LENGTH)
        );

        final String headerName = Strings.padEnd("Name", REPORT_COLUMN_NAME_LENGTH, ' ');
        final String headerDuration = Strings.padEnd("Duration", REPORT_COLUMN_DURATION_LENGTH, ' ');
        final String headerInvocationsCount = Strings.padEnd("Count", REPORT_COLUMN_COUNT_LENGTH, ' ');
        final String headerColumns = String.format("| %s | %s | %s |", headerName, headerDuration, headerInvocationsCount);

        final List<String> rows = checkPoints.stream()
                .map(point -> buildReportRow(point, chronoUnit))
                .collect(Collectors.toList());
        final List<String> list = newArrayList();
        list.add(headerTitle);
        list.add(headerSeparator);
        list.add(headerColumns);
        list.add(headerSeparator);
        list.addAll(rows);
        final String report = String.join("\r\n", list);

        LOGGER.debug("Stopwatcher's report for key '{}' has been generated: \n", key);

        writeReport(key, report);

        return report;
    }

    @SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_BAD_PRACTICE")
    @SneakyThrows
    private static void writeReport(final String key, final String report) {
        final File file = new File("./build/reports/measury/");
        file.mkdirs();
        final String fileName = String.format("%s-%s", URLEncoder.encode(key, "UTF-8"), LocalDateTime.now().format(REPORT_FILE_NAME_FORMATTER));
        final PrintWriter writer = new PrintWriter(String.format("%s/%s.txt", file.getAbsolutePath(), fileName), "UTF-8");
        writer.println(report);
        writer.close();
    }

    private static String buildReportRow(final CheckPoint point, final ChronoUnit chronoUnit) {
        final String indent = Strings.padStart("", point.getIndent() * 2, ' ');
        final String reportText = String.format("%s [%s]", point.getName(), point.getThread());
        final String nameWithIndent = String.format("%s%s", indent, reportText);
        final String rowName = Strings.padEnd(nameWithIndent, REPORT_COLUMN_NAME_LENGTH, '.');

        final String rowDuration = Strings.padStart(getRowDuration(point, chronoUnit), REPORT_COLUMN_DURATION_LENGTH, '.');

        final String rowInvocationsCount = Strings.padStart(String.valueOf(point.getInvocationCount()), REPORT_COLUMN_COUNT_LENGTH, '.');

        return String.format("| %s | %s | %s |", rowName, rowDuration, rowInvocationsCount);
    }

    private static String getRowDuration(final CheckPoint checkPoint, final ChronoUnit chronoUnit) {
        final List<CheckPoint.Execution> executions = checkPoint.getExecutions();
        final long total = executions.stream()
                .mapToLong(exec -> exec.getFinishedAt() - exec.getStartedAt())
                .sum();

        final Duration duration = Duration.of(total, ChronoUnit.MILLIS);

        if (chronoUnit == ChronoUnit.SECONDS) {
            final long hours = duration.toHours();
            final long minutes = duration.toMinutes() - hours * 60;
            final long seconds = duration.getSeconds() - hours * 3600 - minutes * 60;
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        }

        final long hours = duration.toHours();
        final long minutes = duration.toMinutes() - hours * 60;
        final long seconds = duration.getSeconds() - hours * 3600 - minutes * 60;
        final long mills = total - (hours * 3600 + minutes * 60 + seconds) * 1000;
        return String.format("%02d:%02d:%02d.%03d", hours, minutes, seconds, mills);
    }
}
