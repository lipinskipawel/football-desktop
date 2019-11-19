package com.github.lipinskipawel.controller;

final class DataObject {

    private String question;
    private int questionOption;
    private int answer;
    private long time;


    DataObject(final String question, final int questionOption, final int answer, final long time) {
        this.question = question;
        this.questionOption = questionOption;
        this.answer = answer;
        this.time = time;
    }

    DataObject() {
    }

    public String getQuestion() {
        return question;
    }

    public int getQuestionOption() {
        return questionOption;
    }

    public int getAnswer() {
        return answer;
    }

    public long getTime() {
        return time;
    }
}