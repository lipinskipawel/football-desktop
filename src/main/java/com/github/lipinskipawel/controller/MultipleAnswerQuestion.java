package com.github.lipinskipawel.controller;

import java.util.Arrays;

final class MultipleAnswerQuestion implements Question {

    private final String title;
    private final String question;
    private final String[] options;

    MultipleAnswerQuestion(final String title,
                           final String question,
                           final String[] options) {
        this.title = title;
        this.question = question;
        this.options = options;
    }

    @Override
    public String getTitle() {
        return this.title;
    }

    @Override
    public String getQuestion() {
        return this.question;
    }

    @Override
    public String[] getOptions() {
        return Arrays.copyOf(this.options, this.options.length);
    }
}
