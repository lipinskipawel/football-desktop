package com.github.lipinskipawel.controller;

import java.util.List;
import java.util.Random;

final class InMemoryQuestions implements QuestionLoader {


    @Override
    public Question takeYesNoQuestion() {
        final var size = loadQuestions().size();
        return loadQuestions()
                .get(new Random().nextInt(size));
    }

    @Override
    public Question takeMultipleChoiceQuestion() {
        final var size = loadMultipleQuestion().size();
        return loadMultipleQuestion()
                .get(new Random().nextInt(size));
    }

    @Override
    public Question takeAiQuestion() {
        return new MultipleAnswerQuestion("Ai", "How good was ai", new String[]{"0", "1", "2", "3", "4", "5"});
    }

    private List<Question> loadQuestions() {
        final var title = "Yes, No";
        return List.of(
                new OneAnswerQuestion(title, "Do you like music?", new String[]{"Yes", "No"}),
                new OneAnswerQuestion(title, "Do you like pop?", new String[]{"Yes", "No"}),
                new OneAnswerQuestion(title, "Do you like pizza?", new String[]{"Yes", "No"}),
                new OneAnswerQuestion(title, "Do you like pasta?", new String[]{"Yes", "No"}),
                new OneAnswerQuestion(title, "Do you like math?", new String[]{"Yes", "No"})
        );
    }

    private List<Question> loadMultipleQuestion() {
        final var title = "Multiple answer question";
        return List.of(
                new MultipleAnswerQuestion(title, "Which of them do you know", new String[]{"John", "Mark", "Michael"}),
                new MultipleAnswerQuestion(title, "What is the name of the Witcher", new String[]{"Terry", "Jennifer", "Gerald"}),
                new MultipleAnswerQuestion(title, "An apple is?", new String[]{"Fruit", "Meat", "Vegetable"}),
                new MultipleAnswerQuestion(title, "In which league Michael Jordan played?", new String[]{"NBA", "NFL", "NHL"})
        );
    }
}
