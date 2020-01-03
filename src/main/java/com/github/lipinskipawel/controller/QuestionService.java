package com.github.lipinskipawel.controller;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

final class QuestionService {

    private final QuestionLoader questionLoader;
    private final Optional<UUID> uuid;

    public QuestionService(final QuestionLoader questionLoader) {
        this.questionLoader = questionLoader;
        this.uuid = loadUUID(Paths.get("uuid.txt").toFile());
    }

    DataObject displayYesNoCancel(final String module) {
        final var question = this.questionLoader.takeYesNoQuestion();

        final var start = System.currentTimeMillis();

        final var answer = JOptionPane.showConfirmDialog(
                null,
                question.getQuestion(),
                question.getTitle(),
                JOptionPane.YES_NO_CANCEL_OPTION
        );

        final var wordAnswer = translateToWord(answer);
        String uniqIdentifier;
        try {
            uniqIdentifier = getUUID().toString();
        } catch (IOException e) {
            e.printStackTrace();
            uniqIdentifier = "temp";
        }
        return new DataObject(
                uniqIdentifier,
                module,
                question.getQuestion(),
                question.getOptions(),
                wordAnswer,
                System.currentTimeMillis() - start
        );
    }

    DataObject displayMultipleAnswers(final String module) {
        final var question = this.questionLoader.takeMultipleChoiceQuestion();

        final var options = new JList<>(question.getOptions());

        final var start = System.currentTimeMillis();

        final var answers = JOptionPane.QUESTION_MESSAGE;
        JOptionPane.showMessageDialog(null,
                options,
                question.getTitle(),
                answers
        );

        final var wordAnswers = options.getSelectedValuesList();
        String uniqIdentifier;
        try {
            uniqIdentifier = getUUID().toString();
        } catch (IOException e) {
            e.printStackTrace();
            uniqIdentifier = "temp";
        }
        return new DataObject(
                uniqIdentifier,
                module,
                question.getQuestion(),
                question.getOptions(),
                wordAnswers.toArray(String[]::new),
                System.currentTimeMillis() - start
        );
    }

    DataObject displayAiQuestion() {
        final var question = this.questionLoader.takeAiQuestion();

        final var jRadioButtons = Stream.of(question.getOptions())
                .map(JRadioButton::new)
                .collect(toList());

        final var start = System.currentTimeMillis();

        final var panel = new JPanel();
        final var group = new ButtonGroup();
        for (var radio : jRadioButtons) {
            group.add(radio);
            group.add(radio);

            panel.add(radio);
            panel.add(radio);
        }

        JOptionPane.showMessageDialog(null, panel);

        final var selected = jRadioButtons
                .stream()
                .filter(AbstractButton::isSelected)
                .map(AbstractButton::getText)
                .collect(toList());

        String uniqIdentifier;
        try {
            uniqIdentifier = getUUID().toString();
        } catch (IOException e) {
            e.printStackTrace();
            uniqIdentifier = "temp";
        }
        return new DataObject(
                uniqIdentifier,
                "1-ai",
                question.getQuestion(),
                question.getOptions(),
                selected.toArray(String[]::new),
                System.currentTimeMillis() - start
        );
    }

    Optional<UUID> loadUUID(final File file) {
        if (file == null) {
            return Optional.empty();
        }
        if (file.isDirectory()) {
            return Optional.empty();
        }
        if (file.isFile()) {
            try {
                return Files.readAllLines(file.toPath())
                        .stream()
                        .limit(1)
                        .map(UUID::fromString)
                        .findFirst()
                        .or(Optional::empty);
            } catch (IOException e) {
                file.delete();
                return Optional.empty();
            }
        }
        return Optional.empty();
    }

    private UUID getUUID() throws IOException {
        if (uuid.isPresent()) {
            return this.uuid.get();
        }
        return store(UUID.randomUUID());
    }

    private UUID store(final UUID uuid) throws IOException {
        final var path = Paths.get("uuid.txt");
        Files.writeString(path, uuid.toString());
        return uuid;
    }

    private String[] translateToWord(final int answer) {
        return switch (answer) {
            case JOptionPane.YES_OPTION -> new String[]{"Yes"};
            case JOptionPane.NO_OPTION -> new String[]{"No"};
            case JOptionPane.CANCEL_OPTION -> new String[]{"Cancel"};
            default -> new String[]{"X}"};
        };
    }
}
