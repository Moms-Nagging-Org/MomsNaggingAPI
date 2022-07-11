package com.jasik.momsnaggingapi.domain.admin.service;

import com.jasik.momsnaggingapi.domain.admin.Admin;
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

    public Admin.ServiceData getServiceData() {
        Admin.ServiceData serviceData = new Admin.ServiceData();
        serviceData.setNOfUsers(userService.countUser());
        serviceData.setNOfQuestions((long) questionService.findAllQuestions().size());
        return serviceData;
    }

    public Admin.GradeData getGradeData() {
        Admin.GradeData gradeData = new Admin.GradeData();
        // TODO: grade 별 유저 수
        return gradeData;
    }

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
