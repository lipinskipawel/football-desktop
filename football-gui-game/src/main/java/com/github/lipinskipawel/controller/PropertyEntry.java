package com.github.lipinskipawel.controller;

final class PropertyEntry {

    private final String name;
    private final String value;

    PropertyEntry(final String name, final String value) {
        this.name = computeName(name);
        this.value = computeValue(value);
    }

    PropertyEntry(final String entry) {
        final var entries = entry.split("=");
        if (entries.length != 2)
            throw new RuntimeException("Error in parsing entry line. line should be separated by one '='\n" +
                    "number of elements : " + entries.length);
        this.name = computeName(entries[0]);
        this.value = computeValue(entries[1]);
    }

    private String computeName(final String name) {
        return name.trim();
    }

    private String computeValue(final String value) {
        final var trimmedValue = value.trim();
        if (trimmedValue.startsWith("${") && trimmedValue.endsWith("}")) {
            final var env = trimmedValue.replaceAll("\\$\\{|}", "");
            final var envVariable = System.getenv(env);
            if (envVariable != null)
                return envVariable;
            throw new RuntimeException("There is no environment variable with name : " + env);
        }
        return trimmedValue;
    }

    String getName() {
        return name;
    }

    String getValue() {
        return value;
    }
}
