package com.jasik.momsnaggingapi.domain.admin.service;

import com.jasik.momsnaggingapi.domain.admin.Admin;
import com.jasik.momsnaggingapi.domain.grade.Grade;
import com.jasik.momsnaggingapi.domain.grade.service.GradeService;
import com.jasik.momsnaggingapi.domain.nagging.Nagging;
import com.jasik.momsnaggingapi.domain.push.Push;
import com.jasik.momsnaggingapi.domain.push.Push.PushType;
import com.jasik.momsnaggingapi.domain.push.service.PushService;
import com.jasik.momsnaggingapi.domain.question.Interface.QuestionUserInterface;
import com.jasik.momsnaggingapi.domain.question.Question;
import com.jasik.momsnaggingapi.domain.question.service.QuestionService;
import com.jasik.momsnaggingapi.domain.schedule.Interface.ScheduleNaggingInterface;
import com.jasik.momsnaggingapi.domain.schedule.Schedule;
import com.jasik.momsnaggingapi.domain.schedule.service.ScheduleService;
import com.jasik.momsnaggingapi.domain.user.User;
import com.jasik.momsnaggingapi.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
        serviceData.setNOfQuestions(questionService.countAllQuestions());
        return serviceData;
    }

    public Admin.GradeData getGradeData() {
        Admin.GradeData gradeData = new Admin.GradeData();
        HashMap<Long, Integer> userGrades = new HashMap<Long, Integer>();
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

    public Page<User.AdminResponse> getUsers(Pageable pageable, String search) {
        Page<User> userPage = userService.findAllUsers(pageable, search);
        Page<User.AdminResponse> userDataList = userPage.map(m -> User.AdminResponse.builder()
                .id(m.getId())
                .provider(m.getProvider())
                .nickName(m.getNickName())
                .personalId(m.getPersonalId())
                .naggingLevel(m.getNaggingLevel())
                .device(m.getDevice())
                .createdAt(m.getCreatedAt())
                .build());
        return userDataList;
    }

    public Page<Question.SignOutReasonResponse> getSignOutReasons(Pageable pageable) {
        Page<Question> questionPage = questionService.findAllSignOutReasons(pageable);
        return questionPage.map(q -> Question.SignOutReasonResponse.builder()
                .id(q.getId())
                .title(q.getTitle())
                .context(q.getContext())
                .createdAt(q.getCreatedAt())
                .build());
    }

    public Page<Admin.QuestionResponse> getQuestions(Pageable pageable, String search) {
        Page<QuestionUserInterface> questionPage = questionService.findAllQuestions(pageable, search);

        return questionPage.map(q -> Admin.QuestionResponse.builder()
                .id(q.getQuestion().getId())
                .title(q.getQuestion().getTitle())
                .context(q.getQuestion().getContext())
                .createdAt(q.getQuestion().getCreatedAt())
                .userId(q.getUser().getPersonalId())
                .build());
    }

    public Page<Schedule.CategoryListAdminResponse> getTemplateSchedulesByCategory(
        Long categoryId, Pageable pageable) {
        Page<ScheduleNaggingInterface> schedules = scheduleService.getTemplateSchedulesByCategory(categoryId, pageable);

        return schedules.map(s -> Schedule.CategoryListAdminResponse.builder()
                .id(s.getSchedule().getId())
                .scheduleName(s.getSchedule().getScheduleName())
                .level1(s.getNagging().getLevel1())
                .level2(s.getNagging().getLevel2())
                .level3(s.getNagging().getLevel3())
                .build());
    }
}
