package com.jasik.momsnaggingapi.domain.grade.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jasik.momsnaggingapi.domain.grade.Grade;
import com.jasik.momsnaggingapi.domain.grade.repository.GradeRepository;
import com.jasik.momsnaggingapi.domain.schedule.repository.ScheduleRepository;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
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

        int inYear = cal.get(cal.YEAR);
        int inMonth = cal.get(cal.MONTH);
        int inDay = cal.get(cal.DAY_OF_MONTH);

        int yoil = cal.get(cal.DAY_OF_WEEK); //요일나오게하기(숫자로)
        if (yoil != 1) {   //해당요일이 일요일이 아닌경우
            yoil = yoil - 2;
        } else {           //해당요일이 일요일인경우
            yoil = 7;
        }
        inDay = inDay - yoil;

        for (int i = 0; i < 7; i += 6) {
            cal.set(inYear, inMonth, inDay + i);  //
            String y = Integer.toString(cal.get(cal.YEAR));
            String m = Integer.toString(cal.get(cal.MONTH) + 1);
            String d = Integer.toString(cal.get(cal.DAY_OF_MONTH));
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

    public int getGradeLevel(Long userId){
        LocalDate weekAgoDate = LocalDate.now().minusDays(7);
        List<String> daysOfWeek = getDaysOfWeek(weekAgoDate);
        LocalDate startDate = LocalDate.parse(daysOfWeek.get(0), DateTimeFormatter.ISO_DATE);
        LocalDate endDate = LocalDate.parse(daysOfWeek.get(1), DateTimeFormatter.ISO_DATE);
        int doneCount = scheduleRepository.findAllByScheduleDateGreaterThanEqualAndScheduleDateLessThanEqualAndUserIdAndDone(
            startDate, endDate, userId, true).size();
        int totalCount = scheduleRepository.findAllByScheduleDateGreaterThanEqualAndScheduleDateLessThanEqualAndUserId(
            startDate, endDate, userId).size();
        // 평가 로직 : 주간 수행 개수 / 주간 전체 개수 * 100
        long score;
        try{
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

        Long userId = 1L;
        Grade grade;
        Optional<Grade> optionalGrade = gradeRepository.findByCreatedYearAndCreatedWeekAndUserId(
            createdYear, createdWeek, userId);
        grade = optionalGrade.orElseGet(() -> gradeRepository.save(
            Grade.builder().userId(userId).gradeLevel(getGradeLevel(userId)).createdYear(createdYear)
                .createdWeek(createdWeek).build()));

        return modelMapper.map(grade, Grade.GradeResponse.class);
    }
}

