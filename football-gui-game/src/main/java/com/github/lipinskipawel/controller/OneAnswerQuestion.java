package com.github.lipinskipawel.controller;

import java.util.Arrays;
import java.util.Objects;

final class OneAnswerQuestion implements Question {

    private final String title;
    private final String question;
    private final String[] options;


    OneAnswerQuestion(final String title,
                      final String question,
                      final String[] options) {
        this.title = title;
        this.question = question;
        this.options = options;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getQuestion() {
        return question;
    }

    @Override
    public String[] getOptions() {
        return Arrays.copyOf(this.options, this.options.length);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final OneAnswerQuestion that = (OneAnswerQuestion) o;
        return title.equals(that.title) &&
                question.equals(that.question) &&
                Arrays.equals(options, that.options);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(title, question);
        result = 31 * result + Arrays.hashCode(options);
        return result;
    }
}
