package com.jasik.momsnaggingapi.domain.grade.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jasik.momsnaggingapi.domain.grade.Grade;
import com.jasik.momsnaggingapi.domain.grade.Grade.GradeResponse;
import com.jasik.momsnaggingapi.domain.grade.Grade.GradesOfMonthResponse;
import com.jasik.momsnaggingapi.domain.grade.Grade.Performance;
import com.jasik.momsnaggingapi.domain.grade.repository.GradeRepository;
import com.jasik.momsnaggingapi.domain.schedule.Schedule;
import com.jasik.momsnaggingapi.domain.schedule.repository.ScheduleRepository;
import com.jasik.momsnaggingapi.domain.user.User;
import com.jasik.momsnaggingapi.domain.user.repository.UserRepository;
import com.jasik.momsnaggingapi.infra.common.AsyncService;
import com.jasik.momsnaggingapi.infra.common.ErrorCode;
import com.jasik.momsnaggingapi.infra.common.Utils;
import com.jasik.momsnaggingapi.infra.common.exception.ThreadFullException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.concurrent.RejectedExecutionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;


@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GradeService {

    private final GradeRepository gradeRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    private final Utils utils;

    public Grade createGrade(Long userId, LocalDate startDate, LocalDate endDate, int createdYear,
        int createdWeek) {
        List<Grade.Performance> performances = gradeRepository.findPerformanceOfPeriodByUserIdAndStartDateAndEndDate(
            userId, startDate, endDate);
        double sum = 0.0;
        double performanceAvg;
        for (Performance performance : performances) {
            Optional<Integer> optionalInteger = Optional.ofNullable(performance.getAvg());
            if (optionalInteger.isPresent()) {
                performanceAvg = optionalInteger.get();
            } else {
                performanceAvg = 0;
            }
            sum += performanceAvg;
        }
        double avg =
            sum / performances.size();

        int gradeLevel = 5;
        if (90 <= avg) {
            gradeLevel = 1;
        } else if (70 <= avg) {
            gradeLevel = 2;
        } else if (50 <= avg) {
            gradeLevel = 3;
        } else if (30 <= avg) {
            gradeLevel = 4;
        }
        return gradeRepository.save(
            Grade.builder().userId(userId).gradeLevel(gradeLevel).createdYear(createdYear)
                .createdWeek(createdWeek).build());
    }

    public int getAwardLevel(Long userId) {
        int awardLevel = 0;
        long totalCount = gradeRepository.countByUserIdAndGradeLevel(userId, 5);
        switch ((int) totalCount) {
            case 5:
                awardLevel = 1;
                break;
            case 10:
                awardLevel = 2;
                break;
            case 30:
                awardLevel = 3;
                break;
            case 50:
                awardLevel = 4;
                break;
        }
        return awardLevel;
    }
    @Transactional
    public Grade.GradeResponse getGradeOfLastWeek(Long userId) {

        User user = userRepository.findById(userId)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다."));
        LocalDate userCreatedAt = LocalDate.from(user.getCreatedAt());
        if ((userCreatedAt.get(WeekFields.ISO.weekOfYear())) == (LocalDate.now().get(WeekFields.ISO.weekOfYear()))) {
            return new GradeResponse();
        }
        LocalDate weekAgoDate = LocalDate.now().minusDays(7);
        int createdYear = weekAgoDate.getYear();
        int createdWeek = weekAgoDate.get(WeekFields.ISO.weekOfYear());
        int awardLevel = 0;
        boolean isNew = false;
        Grade grade;
        Optional<Grade> optionalGrade = gradeRepository.findByCreatedYearAndCreatedWeekAndUserId(
            createdYear, createdWeek, userId);
        if (optionalGrade.isPresent()) {
            grade = optionalGrade.get();
        } else {
            List<String> daysOfWeek = utils.getDaysOfWeek(weekAgoDate);
            LocalDate startDate = LocalDate.parse(daysOfWeek.get(0), DateTimeFormatter.ISO_DATE);
            LocalDate endDate = LocalDate.parse(daysOfWeek.get(1), DateTimeFormatter.ISO_DATE);
            grade = createGrade(userId, startDate, endDate, createdYear, createdWeek);
            if (grade.getGradeLevel() == 5) {
                awardLevel = getAwardLevel(userId);
            }
            isNew = true;
        }
        Grade.GradeResponse gradeResponse = modelMapper.map(grade, Grade.GradeResponse.class);
        gradeResponse.setAwardLevel(awardLevel);
        gradeResponse.setNewGrade(isNew);
        return gradeResponse;
    }

    @Transactional(readOnly = true)
    public Grade.AwardResponse getAwards(Long userId) {

        return new Grade.AwardResponse(getAwardLevel(userId));
    }

    @Transactional(readOnly = true)
    public List<Grade.Performance> getDailyGradesOfMonth(Long userId, int retrieveYear, int retrieveMonth) {

        LocalDate startDate = LocalDate.of(retrieveYear, retrieveMonth, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        return gradeRepository.findPerformanceOfPeriodByUserIdAndStartDateAndEndDate(userId,
            startDate, endDate);
    }

    @Transactional(readOnly = true)
    public List<GradesOfMonthResponse> getWeeklyGradesOfMonth(Long userId, int retrieveYear, int retrieveMonth) {
        LocalDate startDate = LocalDate.of(retrieveYear, retrieveMonth, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
        int startWeek = startDate.get(WeekFields.ISO.weekOfYear());
        int endWeek = endDate.get(WeekFields.ISO.weekOfYear());

        List<Grade> grades = gradeRepository.findAllByUserIdAndCreatedYearAndCreatedWeekGreaterThanEqualAndCreatedWeekLessThanEqualOrderByCreatedYearAscCreatedWeekAsc(
            userId, retrieveYear, startWeek, endWeek);
        ListIterator<Grade> retrieveItr = grades.listIterator();

        List<GradesOfMonthResponse> response = new ArrayList<>();
        int weekIndex = 1;
        Optional<Integer> gradeOfWeek;
        for (int i = startWeek; i < endWeek; i++) {
            gradeOfWeek = null;
            if (retrieveItr.hasNext()) {
                Grade nextGrade = retrieveItr.next();
                if (nextGrade.getCreatedWeek() == i) {
                    gradeOfWeek = Optional.of(nextGrade.getGradeLevel());
                } else {
                    retrieveItr.previous();
                }
            }
            response.add(new GradesOfMonthResponse(weekIndex, gradeOfWeek));
            weekIndex++;
        }
        return response;
    }

    @Transactional(readOnly = true)
    public Grade.StatisticsResponse getStatistics(Long userId) {

        return gradeRepository.findStatisticsByUserId(userId, LocalDate.now());
    }

    @Transactional
    public List<Grade> getAllGrades() {
        return gradeRepository.findByGradeLevel(5);
    }
}

