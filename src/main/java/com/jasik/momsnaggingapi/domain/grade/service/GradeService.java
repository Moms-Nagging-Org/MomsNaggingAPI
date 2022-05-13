package com.jasik.momsnaggingapi.domain.grade.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jasik.momsnaggingapi.domain.grade.Grade;
import com.jasik.momsnaggingapi.domain.grade.repository.GradeRepository;
import com.jasik.momsnaggingapi.domain.schedule.Schedule;
import com.jasik.momsnaggingapi.domain.schedule.repository.ScheduleRepository;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GradeService {

    private final GradeRepository gradeRepository;
    private final ScheduleRepository scheduleRepository;
    private final ModelMapper modelMapper;
    private final ObjectMapper objectMapper;

    public List<String> getDaysOfWeek(LocalDate localDate) {
        List<String> arrYMD = new ArrayList<String>();
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

    public int getGradeLevel(Long userId) {
        LocalDate weekAgoDate = LocalDate.now().minusDays(7);
        List<String> daysOfWeek = getDaysOfWeek(weekAgoDate);
        LocalDate startDate = LocalDate.parse(daysOfWeek.get(0), DateTimeFormatter.ISO_DATE);
        LocalDate endDate = LocalDate.parse(daysOfWeek.get(1), DateTimeFormatter.ISO_DATE);
        float doneCount = (float) scheduleRepository.findAllByScheduleDateGreaterThanEqualAndScheduleDateLessThanEqualAndUserIdAndDone(
            startDate, endDate, userId, true).size();
        float totalCount = (float) scheduleRepository.findAllByScheduleDateGreaterThanEqualAndScheduleDateLessThanEqualAndUserIdOrderByScheduleDateAscScheduleTimeAsc(
            startDate, endDate, userId).size();
        // 평가 로직 : 주간 수행 개수 / 주간 전체 개수 * 100
        float score;
        try {
            score = (doneCount / totalCount) * 100L;
        } catch (ArithmeticException e) {
            score = 0;
        }
        int gradeLevel = 1;
        if (90 <= score) {
            gradeLevel = 5;
        } else if (70 <= score) {
            gradeLevel = 4;
        } else if (50 <= score) {
            gradeLevel = 3;
        } else if (30 <= score) {
            gradeLevel = 2;
        }

        return gradeLevel;
    }

    @Transactional
    public Grade.GradeResponse getGrade(int createdYear, int createdWeek) {

        // TODO: '수' 등급의 변수 환경변수로 관리

        Long userId = 1L;
        Grade grade;
        int awardLevel = 0;
        Optional<Grade> optionalGrade = gradeRepository.findByCreatedYearAndCreatedWeekAndUserId(
            createdYear, createdWeek, userId);
        if (optionalGrade.isPresent()) {
            grade = optionalGrade.get();
        } else {
            grade = gradeRepository.save(
                Grade.builder().userId(userId).gradeLevel(getGradeLevel(userId))
                    .createdYear(createdYear).createdWeek(createdWeek).build());
            if (grade.getGradeLevel() == 5) {
                awardLevel = getAwardLevel(userId);
            }
        }
        Grade.GradeResponse gradeResponse = modelMapper.map(grade, Grade.GradeResponse.class);
        gradeResponse.setAwardLevel(awardLevel);

        return gradeResponse;
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
    @Transactional(readOnly = true)
    public int getAwards() {

        Long userId = 1L;

        return getAwardLevel(userId);
    }

    @Transactional(readOnly = true)
    public List<HashMap> getMonthly(int retrieveYear, int retrieveMonth) {

        Long userId = 1L;
        LocalDate startDate = LocalDate.of(retrieveYear, retrieveMonth, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        List<LocalDate> durationDate = new ArrayList<>();
        for (LocalDate nextDate = startDate; !nextDate.isAfter(endDate);
            nextDate = nextDate.plusDays(1)) {
            durationDate.add(nextDate);
        }

        List<Schedule> schedules = scheduleRepository.findAllByScheduleDateGreaterThanEqualAndScheduleDateLessThanEqualAndUserIdOrderByScheduleDateAscScheduleTimeAsc(
            startDate, endDate, userId);
        ListIterator<Schedule> retrieveItr = schedules.listIterator();

        // 하루씩 증가
        List<HashMap> result = new ArrayList<>();
        for (LocalDate date : durationDate) {
            HashMap<String, Object> dailyMap = new HashMap<>();
            Schedule retrieveSchedule;
            List<Schedule.ScheduleResponse> scheduleDtoList = new ArrayList<>();
            float doneCount = 0;
            float totalCount = 0;
            int performance;
            while (retrieveItr.hasNext()) {
                retrieveSchedule = retrieveItr.next();
                if (date.isEqual(retrieveSchedule.getScheduleDate())) {
                    totalCount++;
                    if (retrieveSchedule.getDone()) {
                        doneCount++;
                    }
                    scheduleDtoList.add(
                        modelMapper.map(retrieveSchedule, Schedule.ScheduleResponse.class));
                } else {
                    break;
                }
            }
            if (doneCount == 0 || totalCount == 0) {
                performance = 0;
            } else {
                performance = (int) ((doneCount / totalCount) * 100);
            }
            dailyMap.put("date", date);
            dailyMap.put("performance", performance);
            dailyMap.put("schedules", scheduleDtoList);
            result.add(dailyMap);
            retrieveItr.previous();
        }

        return result;
    }
}

