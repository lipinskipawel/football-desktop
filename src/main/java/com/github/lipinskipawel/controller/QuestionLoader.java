package com.github.lipinskipawel.controller;

interface QuestionLoader {

    Question takeYesNoQuestion();

    Question takeMultipleChoiceQuestion();

    Question takeAiQuestion();
}
