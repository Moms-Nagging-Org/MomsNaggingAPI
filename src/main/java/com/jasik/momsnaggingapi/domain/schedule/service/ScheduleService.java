package com.jasik.momsnaggingapi.domain.schedule.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jasik.momsnaggingapi.domain.schedule.Category;
import com.jasik.momsnaggingapi.domain.schedule.Schedule;
import com.jasik.momsnaggingapi.domain.schedule.repository.CategoryRepository;
import com.jasik.momsnaggingapi.domain.schedule.repository.ScheduleRepository;
import java.time.LocalDate;
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
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;
    private final ObjectMapper objectMapper;

    @Transactional
    public Schedule.ScheduleResponse postSchedule(Schedule.ScheduleRequest dto) {

        // 스케줄 원본 저장
        Schedule schedule = scheduleRepository.save(modelMapper.map(dto, Schedule.class));
        // TODO : 생성 -> 업데이트 로직 개선사항 찾기 -> select last_insert_id()
        // 원본 ID 저장
        schedule.initOriginalId();
        Schedule originSchedule = scheduleRepository.save(schedule);
        // 습관 스케줄 저장 로직
        if (originSchedule.getScheduleType() == Schedule.ScheduleType.ROUTINE) {
            // TODO: routine 생성 비동기 실행 -> interface로 구현하면 프록시 개별로 생성 됨,
            createRoutine(originSchedule);
        }
        // TODO: n회 반복 습관 -> 모든 주차의 첫날에 원본으로 생성 -> 수정 시 추적이 불가능함 -> 달성 실패한 주 이후로 생성 불가능 -> 모든 일자에 생성해버림 -> 목표 달성한 주차의 이후 습관 삭제(해당 주차의 모든 미수행 습관 삭제할 지) ->

        return modelMapper.map(originSchedule, Schedule.ScheduleResponse.class);
    }

    @Async
    public void createRoutine(Schedule originSchedule) {

        // 원본 스케줄의 날짜
        LocalDate originScheduleDate = originSchedule.getScheduleDate();
        int dayOfWeekNumber = originScheduleDate.getDayOfWeek().getValue() - 1;
        boolean[] repeatDays = originSchedule.calculateRepeatDays();
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
                Schedule nextSchedule = new Schedule();
                BeanUtils.copyProperties(originSchedule, nextSchedule, "id", "scheduleDate");
                nextSchedule.initScheduleDate(nextScheduleDate);
                nextSchedules.add(nextSchedule);
                log.info("비동기 1");
            }
            weekCount += 1;
        }
        scheduleRepository.saveAll(nextSchedules);
        log.info("비동기 2");
    }

    @Transactional(readOnly = true)
    public List<Schedule.ScheduleListResponse> getSchedules(Long userId, LocalDate scheduleDate) {

//        log.error("test error");
//        log.info("test info");
        List<Schedule> schedules = scheduleRepository.findAllByScheduleDateAndUserId(scheduleDate,
                userId);

        return schedules.stream()
                .map(Schedule -> modelMapper.map(Schedule, com.jasik.momsnaggingapi.domain.schedule.Schedule.ScheduleListResponse.class))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Schedule.ScheduleResponse getSchedule(Long scheduleId) {

        Optional<Schedule> schedule = scheduleRepository.findById(scheduleId);
        return schedule.map(value -> modelMapper.map(value, Schedule.ScheduleResponse.class)).orElse(null);
    }

    @Transactional
    public Schedule.ScheduleResponse patchSchedule(Long scheduleId, JsonPatch jsonPatch) {

        Long userId = 1L;

        Optional<Schedule> optionalTargetSchedule = scheduleRepository.findById(scheduleId);
        if (optionalTargetSchedule.isPresent()) {
            Schedule targetSchedule = optionalTargetSchedule.get();
            // 타겟 스케줄 변경사항 적용
            Schedule modifiedSchedule = scheduleRepository.save(
                    mergeSchedule(targetSchedule, jsonPatch));
            ArrayList<String> columnList = new ArrayList<>();
            for (JsonValue i : jsonPatch.toJsonArray()) {
                columnList.add(String.valueOf(i.asJsonObject().get("path")).replaceAll("\"", "").replaceAll("/", ""));
            }
//            if (columnList.contains("/done")) {
//                boolean value = Boolean.parseBoolean(String.valueOf(i.asJsonObject().get("value")));
//                // n회 반복 습관의 수행 완료 처리인 경우
//                if ((value)
//                    && (modifiedSchedule.getScheduleType() == ScheduleType.ROUTINE)
//                    && (modifiedSchedule.getGoalCount() > 0)) {
//                    Optional<Schedule> optionalOriginSchedule = scheduleRepository.findById(modifiedSchedule.getOriginalId());
//                    if (optionalOriginSchedule.isPresent()) {
//                        // 원본 스케줄
//                        Schedule originSchedule = optionalOriginSchedule.get();
//                        int originWeek = originSchedule.getScheduleDate().get(WeekFields.ISO.weekOfYear());
//                        int targetWeek = modifiedSchedule.getScheduleDate().get(WeekFields.ISO.weekOfYear());
//                        if (originWeek == targetWeek){
//                            // 목표 미완 and 내일 주차 == 원본 주차
//                            if (!originSchedule.plusDoneCount()
//                                && modifiedSchedule.getScheduleDate().plusDays(1).get(WeekFields.ISO.weekOfYear()) == originWeek){
//                                // 다음날 한개 더 생성
//                                modifiedSchedule.initNextSchedule();
//                                scheduleRepository.save(modifiedSchedule);
//                            }
//                        }
            // 반복 옵션 수정이 포함된 경우 삭제 후 재 생성
            if (columnList.contains("mon") || columnList.contains("tue") || columnList.contains(
                "wed") || columnList.contains("thu") || columnList.contains("fri")
                || columnList.contains("sat") || columnList.contains("sun")) {
                scheduleRepository.deleteWithIdAfter(modifiedSchedule.getId(),
                    modifiedSchedule.getUserId(), modifiedSchedule.getOriginalId());
                modifiedSchedule.initOriginalId();
                scheduleRepository.save(modifiedSchedule);
                // TODO: aysnc
                createRoutine(modifiedSchedule);
            }
            // 반복 옵션은 수정하지 않고 이름, 시간대, 알람시간 만 수정하는 경우 -> 이후 스케줄도 업데이트
            else if (columnList.contains("scheduleName") || columnList.contains("scheduleTime")
                || columnList.contains("alarmTime")) {
                scheduleRepository.updateWithIdAfter(modifiedSchedule.getScheduleName(),
                    modifiedSchedule.getScheduleTime(), modifiedSchedule.getAlarmTime(),
                    modifiedSchedule.getId(), modifiedSchedule.getUserId(),
                    modifiedSchedule.getOriginalId());
            }
            return modelMapper.map(modifiedSchedule, Schedule.ScheduleResponse.class);
        } else {
            return null;
        }
    }

    private Schedule mergeSchedule(Schedule originalSchedule, JsonPatch jsonPatch) {

        JsonStructure target = objectMapper.convertValue(originalSchedule, JsonStructure.class);
        JsonValue patchedSchedule = jsonPatch.apply(target);

        return objectMapper.convertValue(patchedSchedule, Schedule.class);
    }

    @Transactional
    public void deleteSchedule(Long scheduleId) {

        Long userId = 1L;
        Optional<Schedule> schedule = scheduleRepository.findByIdAndUserId(userId, scheduleId);
        schedule.ifPresent(value -> scheduleRepository.deleteWithIdAfter(scheduleId, userId,
            value.getOriginalId()));
    }

    @Transactional
    public List<Schedule.ScheduleListResponse> postSchedulesArray(List<Long> scheduleArrayRequest) {

        List<Schedule.ScheduleListResponse> scheduleAllResponses = new ArrayList<>();
        scheduleAllResponses.add(new Schedule.ScheduleListResponse());

        return scheduleAllResponses;
    }

    @Transactional
    public Category.CategoryResponse postCategory(Category.CategoryRequest dto) {


        Optional<Category> nullCategory = categoryRepository.findByCategoryName(dto.getCategoryName());
        if (nullCategory.isPresent()) return null;
        Long userId = 1L;
        Category category = modelMapper.map(dto, Category.class);
        category.initUserId(userId);
        Category newCategory = categoryRepository.save(category);

        return modelMapper.map(newCategory, Category.CategoryResponse.class);
    }

    @Transactional(readOnly = true)
    public List<Category.CategoryResponse> getCategories() {

        List<Category> categories = categoryRepository.findAllByUsed(true);

        return categories.stream()
                .map(Category -> modelMapper.map(Category, com.jasik.momsnaggingapi.domain.schedule.Category.CategoryResponse.class))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Schedule.CategoryListResponse> getCategorySchedules(Long categoryId) {

        List<Schedule> schedules = scheduleRepository.findAllByCategoryId(categoryId);

        return schedules.stream()
                .map(Schedule -> modelMapper.map(Schedule, com.jasik.momsnaggingapi.domain.schedule.Schedule.CategoryListResponse.class))
                .collect(Collectors.toList());
    }
}