package com.github.lipinskipawel.controller;

import com.google.gson.Gson;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public final class HerokuService {

    void send(final DataObject dataObject) {
        final var loader = PropertyLoader.load();
        final var client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        final var request = HttpRequest.newBuilder()
                .uri(URI.create(loader.get("storage.service") + "question/save"))
                .timeout(Duration.ofMinutes(1))
                .header(loader.get("token.name"), loader.get("token.value"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers
                        .ofString(
                                new Gson().toJson(dataObject))
                )
                .build();
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
    }

    public static HttpResponse<Void> wakeUpHeroku() {
        final var loader = PropertyLoader.load();
        final var ping = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        final var pong = HttpRequest.newBuilder()
                .uri(URI.create(loader.get("storage.service") + "actuator/health"))
                .timeout(Duration.ofMinutes(2))
                .header(loader.get("token.name"), loader.get("token.value"))
                .GET()
                .build();
        return ping.sendAsync(pong, HttpResponse.BodyHandlers.discarding()).join();
    }
}
