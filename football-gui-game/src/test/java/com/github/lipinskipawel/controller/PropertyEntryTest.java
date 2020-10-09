package com.github.lipinskipawel.controller;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.junit.jupiter.api.parallel.Execution;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

@DisplayName("Internal -- PropertyEntry")
class PropertyEntryTest {

    @Test
    @DisplayName("passing name and value")
    void twoArgsNoEnvVariable() {
        final var entry = new PropertyEntry("token", "value");

        assertAll("not handle two constructor",
                () -> Assertions.assertThat(entry.getName()).isEqualTo("token"),
                () -> Assertions.assertThat(entry.getValue()).isEqualTo("value")
        );
    }

    @Test
    @DisplayName("passing one-liner")
    void oneLineNoEnvVariable() {
        final var oneLine = "nice.token.name=value.token";
        final var entry = new PropertyEntry(oneLine);

        assertAll("Not parsing env variable",
                () -> Assertions.assertThat(entry.getName()).isEqualTo("nice.token.name"),
                () -> Assertions.assertThat(entry.getValue()).isEqualTo("value.token")
        );
    }

    @Disabled
    @Test
    @DisplayName("passing name and (value - sys env)")
    @EnabledIf("'token' == systemEnvironment.get('token')")
    void twoArgsWithEnvVariable() {
        final var entry = new PropertyEntry("random", "${token}");

        assertAll("Not parsing env variable",
                () -> Assertions.assertThat(entry.getName()).isEqualTo("random"),
                () -> Assertions.assertThat(entry.getValue()).isEqualTo("token")
        );
    }

    @Test
    @DisplayName("passing one-liner sys env")
    void oneLinerWithEnvVariable() {
        assumeTrue("token".equals(System.getenv("token")));

        final var oneLine = "nice.token.name=${token}";
        final var entry = new PropertyEntry(oneLine);

        assertAll("Not parsing env variable",
                () -> Assertions.assertThat(entry.getName()).isEqualTo("nice.token.name"),
                () -> Assertions.assertThat(entry.getValue()).isEqualTo("token")
        );
    }
}
