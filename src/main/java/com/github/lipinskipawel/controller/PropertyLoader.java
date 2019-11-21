package com.github.lipinskipawel.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
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

    static PropertyLoader load() {
        final var inStream = PropertyLoader.class
                .getResourceAsStream("/application.properties");
        final var map = new BufferedReader(new InputStreamReader(inStream))
                .lines()
                .map(PropertyEntry::new)
                .collect(Collectors.toMap(PropertyEntry::getName, PropertyEntry::getValue));
        return new PropertyLoader(map);
    }
}
