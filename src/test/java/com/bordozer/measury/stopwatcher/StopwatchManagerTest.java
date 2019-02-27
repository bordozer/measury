package com.bordozer.measury.stopwatcher;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StopwatchManagerTest {

    @Test
    void shouldCreateNewStopwatcher() {
        // given

        // when
        final Stopwatcher stopwatcher = StopwatchManager.getInstance("KEY1");

        // then
        assertThat(stopwatcher).isNotNull();
        assertThat(stopwatcher.getKey()).isEqualTo("KEY1");
        assertThat(stopwatcher.getCurrentIndent()).isEqualTo(0);
        assertThat(stopwatcher.getCheckpoints()).isNotNull().hasSize(0);
    }
}
