package com.github.lipinskipawel.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.stream.Collectors;

final class PropertyLoader {

    private final Map<String, String> map;

    private PropertyLoader(final Map<String, String> map) {
        this.map = map;
    }

    String get(final String name) {
        return map.get(name);
    }

    static PropertyLoader load() throws IOException {
        final var file = PropertyLoader.class
                .getResource("/application.properties")
                .getFile();
        final var map = Files.readAllLines(Paths.get(file))
                .stream()
                .map(PropertyEntry::new)
                .collect(Collectors.toMap(PropertyEntry::getName, PropertyEntry::getValue));
        return new PropertyLoader(map);
    }
}
