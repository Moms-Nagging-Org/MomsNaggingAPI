package com.jasik.momsnaggingapi.domain.schedule.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jasik.momsnaggingapi.domain.schedule.Category;
import com.jasik.momsnaggingapi.domain.schedule.Schedule;
import com.jasik.momsnaggingapi.domain.schedule.repository.ScheduleRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.json.JsonPatch;
import javax.json.JsonStructure;
import javax.json.JsonValue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final ModelMapper modelMapper;
    private final ObjectMapper objectMapper;

    @Transactional
    public Schedule.Response postSchedule(Schedule.Request dto) {

        // 스케줄 원본 저장
        Schedule schedule = scheduleRepository.save(modelMapper.map(dto, Schedule.class));
        // TODO : 생성 -> 업데이트 로직 개선사항 찾기 -> select last_insert_id()
        // 원본 ID 저장
        Long originalId = schedule.getId();
        schedule.initOriginalId(originalId);
        Schedule originSchedule = scheduleRepository.save(schedule);

        // 습관 스케줄 저장 로직
        if (originSchedule.getScheduleType() == Schedule.ScheduleType.ROUTINE) {
            // TODO: routine 생성 비동기 실행 -> interface로 구현하면 프록시 개별로 생성 됨,
            createRoutine(originSchedule, dto);
        }

        return modelMapper.map(originSchedule, Schedule.Response.class);
    }

    @Async
    public void createRoutine(Schedule originSchedule, Schedule.Request dto) {

        // 원본 스케줄의 날짜, 알람시간
        LocalDate originScheduleDate = originSchedule.getScheduleDate();
        Optional<LocalDateTime> originAlarmTime = Optional.ofNullable(
            originSchedule.getAlarmTime());

        int dayOfWeekNumber = originScheduleDate.getDayOfWeek().getValue() - 1;
        boolean[] repeatDays = originSchedule.getRepeatDays();
        int nextDay = (7 - dayOfWeekNumber);
        ArrayList<Integer> nextDayList = new ArrayList<>();
        // 반복 요일마다 기준 날짜에서 더해야 하는 일수
        for (boolean i : repeatDays) {
            if (i) {
                nextDayList.add(nextDay);
            }
            nextDay += 1;
        }
        List<Schedule> nextSchedules = new ArrayList<>();
        int weekCount = 0;
        boolean limitDateFlag = true;
        while (limitDateFlag) {
            // 7일씩 더해야 함
            int nextWeek = 7 * weekCount;
            // 반복 요일마다 주차 더함
            for (int i : nextDayList) {
                long plusDays = i + nextWeek;
                LocalDate nextScheduleDate = originScheduleDate.plusDays(plusDays);
                // 1년 후까지만 생성함
                // TODO: 추가 년수 환경변수로 사용하기
                if (nextScheduleDate.isAfter(originScheduleDate.plusYears(1))) {
                    limitDateFlag = false;
                    break;
                }
                dto.setScheduleDate(nextScheduleDate);
                originAlarmTime.ifPresent(
                    localDateTime -> dto.setAlarmTime(localDateTime.plusDays(plusDays)));
                Schedule nextSchedule = modelMapper.map(dto, Schedule.class);
                nextSchedule.initOriginalId(originSchedule.getOriginalId());
                nextSchedules.add(nextSchedule);
                log.info("비동기 1");
            }
            weekCount += 1;
        }
        scheduleRepository.saveAll(nextSchedules);
        log.info("비동기 2");
    }

    @Transactional(readOnly = true)
    public List<Schedule.ListResponse> getSchedules(Long userId, LocalDate scheduleDate) {

//        log.error("test error");
//        log.info("test info");
        List<Schedule> schedules = scheduleRepository.findAllByScheduleDateAndUserId(scheduleDate,
            userId);

        return schedules.stream()
            .map(Schedule -> modelMapper.map(Schedule, Schedule.ListResponse.class))
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Schedule.Response getSchedule(Long scheduleId) {

        Optional<Schedule> schedule = scheduleRepository.findById(scheduleId);
        return schedule.map(value -> modelMapper.map(value, Schedule.Response.class)).orElse(null);
    }

    @Transactional
    public Schedule.Response putSchedule(Long scheduleId, JsonPatch jsonPatch) {

        // 해당 스케줄만 수정
        // 해당 스케줄의 원본이 같은 스케줄 모두 수정
        // 컬럼은?
        Optional<Schedule> originalSchedule = scheduleRepository.findById(scheduleId);
        Schedule modifiedSchedule = mergeSchedule(originalSchedule, jsonPatch); //패치 처리
        scheduleRepository.save(modifiedSchedule);

        return modelMapper.map(originalSchedule, Schedule.Response.class);
    }

    private Schedule mergeSchedule(Optional<Schedule> originalSchedule, JsonPatch jsonPatch) {

        JsonStructure target = objectMapper.convertValue(originalSchedule, JsonStructure.class);
        JsonValue patchedSchedule = jsonPatch.apply(target);

        return objectMapper.convertValue(patchedSchedule, Schedule.class);
    }

    @Transactional
    public void deleteSchedule(Long scheduleId) {

        Long userId = 1L;
        Optional<Schedule> schedule = scheduleRepository.findById(scheduleId);
        schedule.ifPresent(value -> scheduleRepository.deleteWithIdAfter(scheduleId, userId,
            value.getOriginalId()));
        // TODO : Exception 추가
    }

    @Transactional
    public List<Schedule.ListResponse> postSchedulesArray(List<Long> scheduleArrayRequest) {

        List<Schedule.ListResponse> scheduleAllResponses = new ArrayList<>();
        scheduleAllResponses.add(new Schedule.ListResponse());

        return scheduleAllResponses;
    }

    @Transactional(readOnly = true)
    public List<Category.Response> getCategories() {

        List<Category.Response> categoriesResponses = new ArrayList<>();
        categoriesResponses.add(new Category.Response());

        return categoriesResponses;
    }

    @Transactional(readOnly = true)
    public List<Category.ScheduleResponse> getCategorySchedules(Long categoryId) {

        List<Category.ScheduleResponse> scheduleResponses = new ArrayList<>();
        scheduleResponses.add(new Category.ScheduleResponse());

        return scheduleResponses;
    }
}