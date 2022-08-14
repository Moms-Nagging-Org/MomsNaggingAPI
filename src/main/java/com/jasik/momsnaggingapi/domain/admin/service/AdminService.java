package com.jasik.momsnaggingapi.domain.admin.service;

import com.jasik.momsnaggingapi.domain.admin.Admin;
import com.jasik.momsnaggingapi.domain.grade.Grade;
import com.jasik.momsnaggingapi.domain.grade.service.GradeService;
import com.jasik.momsnaggingapi.domain.nagging.Nagging;
import com.jasik.momsnaggingapi.domain.push.Push;
import com.jasik.momsnaggingapi.domain.push.Push.PushType;
import com.jasik.momsnaggingapi.domain.push.service.PushService;
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

import java.util.Collections;
import java.util.HashMap;
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
    private final GradeService gradeService;

    public Admin.ServiceData getServiceData() {
        Admin.ServiceData serviceData = new Admin.ServiceData();
        serviceData.setNOfUsers(userService.countUser());
        serviceData.setNOfQuestions((long) questionService.findAllQuestions().size());
        return serviceData;
    }

    public Admin.GradeData getGradeData() {
        Admin.GradeData gradeData = new Admin.GradeData();
        HashMap<Long, Integer> userGrades = new HashMap<Long, Integer>();
        // TODO: grade 별 유저 수
        List<Grade> allGrades = gradeService.getAllGrades();
        for (int i = 0; i < allGrades.size(); i++) {
            if (userGrades.containsKey(allGrades.get(i).getUserId())) { // 이미 있을 때
                userGrades.put(allGrades.get(i).getUserId(),
                    userGrades.get(allGrades.get(i).getUserId()) + 1);
            } else {
                userGrades.put(allGrades.get(i).getUserId(), 1);
            }
        }

        gradeData.setNOfLv1(Collections.frequency(userGrades.values(), 5));
        gradeData.setNOfLv2(Collections.frequency(userGrades.values(), 10));
        gradeData.setNOfLv3(Collections.frequency(userGrades.values(), 30));
        gradeData.setNOfLv4(Collections.frequency(userGrades.values(), 50));

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

    public List<Schedule.CategoryListAdminResponse> getTemplateSchedulesByCategory(
        Long categoryId) {
        return scheduleService.getTemplateSchedulesByCategory(categoryId);
    }
}
