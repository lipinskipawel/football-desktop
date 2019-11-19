package com.github.lipinskipawel.controller;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class PropertyEntryTest {

    @Test
    void twoArgsNoEnvVariable() {
        final var entry = new PropertyEntry("token", "value");

        assertAll("not handle two constructor",
                () -> Assertions.assertThat(entry.getName()).isEqualTo("token"),
                () -> Assertions.assertThat(entry.getValue()).isEqualTo("value")
        );
    }

    @Test
    void oneLineNoEnvVariable() {
        final var oneLine = "nice.token.name=value.token";
        final var entry = new PropertyEntry(oneLine);

        assertAll("Not parsing env variable",
                () -> Assertions.assertThat(entry.getName()).isEqualTo("nice.token.name"),
                () -> Assertions.assertThat(entry.getValue()).isEqualTo("value.token")
        );
    }

    @Test
    @EnabledIf("'token' == systemEnvironment.get('token')")
    void twoArgsWithEnvVariable() {
        final var entry = new PropertyEntry("random", "${token}");

        assertAll("Not parsing env variable",
                () -> Assertions.assertThat(entry.getName()).isEqualTo("random"),
                () -> Assertions.assertThat(entry.getValue()).isEqualTo("token")
        );
    }

    @Test
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
