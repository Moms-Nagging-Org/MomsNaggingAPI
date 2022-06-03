package com.jasik.momsnaggingapi.domain.admin.service;

import com.jasik.momsnaggingapi.domain.question.Question;
import com.jasik.momsnaggingapi.domain.question.service.QuestionService;
import com.jasik.momsnaggingapi.domain.user.User;
import com.jasik.momsnaggingapi.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AdminService {
    private final UserService userService;
    private final QuestionService questionService;

    public List<User.AdminResponse> getUsers() {
        return userService.findAllUsers();
    }

    public List<Question.SignOutReasonResponse> getSignOutReasons() {
        return questionService.findAllSignOutReasons();
    }

    public List<Question.QuestionResponse> getQuestions() {
        return questionService.findAllQuestions();
    }
}
