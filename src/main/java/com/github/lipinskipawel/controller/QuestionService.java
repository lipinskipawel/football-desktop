package com.github.lipinskipawel.controller;

import javax.swing.*;

final class QuestionService {

    private final QuestionLoader questionLoader;

    public QuestionService(final QuestionLoader questionLoader) {
        this.questionLoader = questionLoader;
    }

    DataObject displayYesNoCancel() {
        final var question = this.questionLoader.takeYesNoQuestion();

        final var start = System.currentTimeMillis();

        final var answer = JOptionPane.showConfirmDialog(
                null,
                question.getQuestion(),
                question.getTitle(),
                JOptionPane.YES_NO_CANCEL_OPTION
        );

        final var wordAnswer = translateToWord(answer);
        return new DataObject(question.getQuestion(), question.getOptions(), wordAnswer, System.currentTimeMillis() - start);
    }

    DataObject displayMultipleAnswers() {
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
        return new DataObject(question.getQuestion(), question.getOptions(), wordAnswers.toArray(String[]::new), System.currentTimeMillis() - start);
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
