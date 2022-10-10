package com.jasik.momsnaggingapi.domain.question.Interface;

import com.jasik.momsnaggingapi.domain.question.Question;
import com.jasik.momsnaggingapi.domain.user.User;

public interface QuestionUserInterface {
    Question getQuestion();
    User getUser();
}
