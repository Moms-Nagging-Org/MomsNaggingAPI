package com.jasik.momsnaggingapi.domain.admin.service;

import com.jasik.momsnaggingapi.domain.question.Question;
import com.jasik.momsnaggingapi.domain.question.service.QuestionService;
import com.jasik.momsnaggingapi.domain.schedule.Schedule;
import com.jasik.momsnaggingapi.domain.schedule.service.ScheduleService;
import com.jasik.momsnaggingapi.domain.user.User;
import com.jasik.momsnaggingapi.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AdminService {
    private final UserService userService;
    private final QuestionService questionService;
    private final ScheduleService scheduleService;

    public List<User.AdminResponse> getUsers() {
        return userService.findAllUsers();
    }

    public List<Question.SignOutReasonResponse> getSignOutReasons() {
        return questionService.findAllSignOutReasons();
    }

    public List<Question.QuestionResponse> getQuestions() {
        return questionService.findAllQuestions();
    }

    public List<Schedule.CategoryListAdminResponse> getTemplateSchedulesByCategory(Long categoryId) {
        return scheduleService.getTemplateSchedulesByCategory(categoryId);
    }
}
