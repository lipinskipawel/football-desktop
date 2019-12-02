package com.github.lipinskipawel.controller;

import java.util.Arrays;

final class DataObject {

    private String question;
    private String[] choices;
    private String[] answers;
    private long time;


    DataObject(final String question,
               final String[] choices,
               final String[] answers,
               final long time) {
        this.question = question;
        this.choices = Arrays.copyOf(choices, choices.length);
        this.answers = answers;
        this.time = time;
    }

    DataObject() {
    }

    public String getQuestion() {
        return question;
    }

    public String[] getChoices() {
        return Arrays.copyOf(choices, choices.length);
    }

    public String[] getAnswer() {
        return Arrays.copyOf(answers, answers.length);
    }

    public long getTime() {
        return time;
    }
}
