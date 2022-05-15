package com.jasik.momsnaggingapi.domain.grade.service;

import com.jasik.momsnaggingapi.domain.common.AsyncService;
import com.jasik.momsnaggingapi.domain.grade.Grade;
import com.jasik.momsnaggingapi.domain.grade.Grade.GradesOfMonthResponse;
import com.jasik.momsnaggingapi.domain.grade.Grade.Performance;
import com.jasik.momsnaggingapi.domain.grade.repository.GradeRepository;
import com.jasik.momsnaggingapi.domain.schedule.Schedule;
import com.jasik.momsnaggingapi.domain.schedule.repository.ScheduleRepository;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GradeService {

    private final GradeRepository gradeRepository;
    private final ScheduleRepository scheduleRepository;
    private final ModelMapper modelMapper;
    private final AsyncService asyncService;

    public List<String> getDaysOfWeek(LocalDate localDate) {
        List<String> arrYMD = new ArrayList<>();
        Date date = java.sql.Date.valueOf(localDate);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        int inYear = cal.get(Calendar.YEAR);
        int inMonth = cal.get(Calendar.MONTH);
        int inDay = cal.get(Calendar.DAY_OF_MONTH);

        int yoil = cal.get(Calendar.DAY_OF_WEEK); //요일나오게하기(숫자로)
        if (yoil != 1) {   //해당요일이 일요일이 아닌경우
            yoil = yoil - 2;
        } else {           //해당요일이 일요일인경우
            yoil = 7;
        }
        inDay = inDay - yoil;

        for (int i = 0; i < 7; i += 6) {
            cal.set(inYear, inMonth, inDay + i);  //
            String y = Integer.toString(cal.get(Calendar.YEAR));
            String m = Integer.toString(cal.get(Calendar.MONTH) + 1);
            String d = Integer.toString(cal.get(Calendar.DAY_OF_MONTH));
            if (m.length() == 1) {
                m = "0" + m;
            }
            if (d.length() == 1) {
                d = "0" + d;
            }

            arrYMD.add(y + "-" + m + "-" + d);
        }

        return arrYMD;
    }

    public void createNRoutines(Long userId, LocalDate startDate, LocalDate endDate) {
        List<Schedule> nRoutineSchedules = scheduleRepository.findAllByUserIdAndGoalCountGreaterThanAndScheduleDateGreaterThanEqualAndScheduleDateLessThanEqual(
            userId, 0, startDate, endDate);
        for (Schedule schedule : nRoutineSchedules) {
            Schedule newSchedule = Schedule.builder().build();
            BeanUtils.copyProperties(schedule, newSchedule, "id", "scheduleDate");
            newSchedule.initScheduleDate(LocalDate.now());
            newSchedule = scheduleRepository.save(newSchedule);
            newSchedule.initOriginalId();
            scheduleRepository.save(newSchedule);
        }
    }

    public Grade createGrade(Long userId, LocalDate startDate, LocalDate endDate, int createdYear,
        int createdWeek) {
        List<Grade.Performance> performances = gradeRepository.findPerformanceOfPeriodByUserIdAndStartDateAndEndDate(
            userId, startDate, endDate);
        double avg =
            performances.stream().mapToDouble(Performance::getAvg).sum() / performances.size();

        int gradeLevel = 1;
        if (90 <= avg) {
            gradeLevel = 5;
        } else if (70 <= avg) {
            gradeLevel = 4;
        } else if (50 <= avg) {
            gradeLevel = 3;
        } else if (30 <= avg) {
            gradeLevel = 2;
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
    public Grade.GradeResponse getGradeOfLastWeek() {

        // TODO: '수' 등급의 변수 환경변수로 관리
        // 프론트 로직 : 로그인 시 마지막 평가 주차 조회(16주차) -> 프론트에서 오늘의 주차 계산(18주차) -> 오늘 주차(18주차) - 1 != 마지막 평가 주차(16) -> 주간 평가 요청
        Long userId = 1L;
        LocalDate weekAgoDate = LocalDate.now().minusDays(7);
        int createdYear = weekAgoDate.getYear();
        int createdWeek = weekAgoDate.get(WeekFields.ISO.weekOfYear());
        int awardLevel = 0;

        Grade grade;
        Optional<Grade> optionalGrade = gradeRepository.findByCreatedYearAndCreatedWeekAndUserId(
            createdYear, createdWeek, userId);
        if (optionalGrade.isPresent()) {
            grade = optionalGrade.get();
        } else {
            List<String> daysOfWeek = getDaysOfWeek(weekAgoDate);
            LocalDate startDate = LocalDate.parse(daysOfWeek.get(0), DateTimeFormatter.ISO_DATE);
            LocalDate endDate = LocalDate.parse(daysOfWeek.get(1), DateTimeFormatter.ISO_DATE);

            // 해당 주차의 n회 습관 생성 -> 비동기
            asyncService.run(()->createNRoutines(userId, startDate, endDate));
            // 주간 평가 생성
            grade = createGrade(userId, startDate, endDate, createdYear, createdWeek);
            if (grade.getGradeLevel() == 5) {
                awardLevel = getAwardLevel(userId);
            }
        }
        Grade.GradeResponse gradeResponse = modelMapper.map(grade, Grade.GradeResponse.class);
        gradeResponse.setAwardLevel(awardLevel);

        return gradeResponse;
    }

    @Transactional(readOnly = true)
    public Grade.AwardResponse getAwards() {

        Long userId = 1L;

        return new Grade.AwardResponse(getAwardLevel(userId));
    }

    @Transactional(readOnly = true)
    public List<Grade.Performance> getDailyGradesOfMonth(int retrieveYear, int retrieveMonth) {

        Long userId = 1L;
        LocalDate startDate = LocalDate.of(retrieveYear, retrieveMonth, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        return gradeRepository.findPerformanceOfPeriodByUserIdAndStartDateAndEndDate(userId,
            startDate, endDate);
    }

    @Transactional(readOnly = true)
    public List<GradesOfMonthResponse> getWeeklyGradesOfMonth(int retrieveYear, int retrieveMonth) {
        Long userId = 1L;
        LocalDate startDate = LocalDate.of(retrieveYear, retrieveMonth, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
        int startWeek = startDate.get(WeekFields.ISO.weekOfYear());
        int endWeek = endDate.get(WeekFields.ISO.weekOfYear());

        List<Grade> grades = gradeRepository.findAllByUserIdAndCreatedYearAndCreatedWeekGreaterThanEqualAndCreatedWeekLessThanEqualOrderByCreatedYearAscCreatedWeekAsc(
            userId, retrieveYear, startWeek, endWeek);
        ListIterator<Grade> retrieveItr = grades.listIterator();

        List<GradesOfMonthResponse> response = new ArrayList<>();
        int weekIndex = 1;
        int gradeOfWeek;
        for (int i = startWeek; i <= endWeek; i++) {
            gradeOfWeek = 1;
            if (retrieveItr.hasNext()) {
                Grade nextGrade = retrieveItr.next();
                if (nextGrade.getCreatedWeek() == i) {
                    gradeOfWeek = nextGrade.getGradeLevel();
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
    public Grade.StatisticsResponse getStatistics() {
        Long userId = 1L;

        return gradeRepository.findStatisticsByUserId(userId);
    }
}

