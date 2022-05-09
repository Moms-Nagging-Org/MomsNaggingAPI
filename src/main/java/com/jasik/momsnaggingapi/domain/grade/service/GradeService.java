package com.jasik.momsnaggingapi.domain.grade.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jasik.momsnaggingapi.domain.grade.Grade;
import com.jasik.momsnaggingapi.domain.grade.repository.GradeRepository;
import com.jasik.momsnaggingapi.domain.schedule.repository.ScheduleRepository;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
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

    @Transactional(readOnly = true)
    public Grade.GradeResponse getGrade(int createdYear, int createdWeek) {

        Long userId = 1L;

        Optional<Grade> optionalGrade = gradeRepository.findByCreatedYearAndCreatedWeekAndUserId(
            createdYear, createdWeek, userId);
        if (optionalGrade.isPresent()) {
            Grade grade = optionalGrade.get();
        } else {
            java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyy-MM-dd");

            // TODO: 주간평가 생성
            // 저번 주차 일주일
            LocalDate weekAgoDate = LocalDate.now().minusDays(7);
            Calendar cal = Calendar.getInstance();
            cal.set(weekAgoDate.getYear(), weekAgoDate.getMonthValue(),
                weekAgoDate.getDayOfMonth()); //연도 설정
            //일주일의 첫날 선택
            cal.setFirstDayOfWeek(Calendar.MONDAY);
            //해당 주차 시작일과의 차이 구하기용
            int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - cal.getFirstDayOfWeek();
            //해당 주차의 첫날 세팅
            cal.add(Calendar.DAY_OF_MONTH, -dayOfWeek);
            //해당 주차의 첫일자
            String stDt = formatter.format(cal.getTime());
            //해당 주차의 마지막 세팅
            cal.add(Calendar.DAY_OF_MONTH, 6);
            //해당 주차의 마지막일자
            String edDt = formatter.format(cal.getTime());
            // 평가 로직 : 일주일동안 수행 개수 / 일주일동안 전체 개수
            LocalDate startDate = LocalDate.parse(stDt, DateTimeFormatter.ISO_DATE);
            LocalDate endDate = LocalDate.parse(edDt, DateTimeFormatter.ISO_DATE).plusDays(1);

            Long doneCount = scheduleRepository.selectDoneCountBetweenDate(startDate, endDate,
                userId);
            Long totalCount = scheduleRepository.selectTotalCountBetweenDate(startDate, endDate,
                userId);
            Long score = (doneCount / totalCount) * 100;
            int gradeLevel = 1;
            if (90 <= score) {
                gradeLevel = 5;
            } else if (70 <= score && score < 90) {
                gradeLevel = 4;
            } else if (50 <= score && score < 70) {
                gradeLevel = 3;
            } else if (30 <= score && score < 50) {
                gradeLevel = 2;
            }
            Grade grade = gradeRepository.save(
                Grade.builder()
                    .userId(userId)
                    .gradeLevel(gradeLevel)
                    .createdYear(createdYear)
                    .createdWeek(createdWeek)
                    .build());
        }
        return modelMapper.map(grade, Grade.GradeResponse.class);

    }

