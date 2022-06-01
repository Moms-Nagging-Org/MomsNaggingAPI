package com.jasik.momsnaggingapi.domain.schedule.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jasik.momsnaggingapi.domain.schedule.Category;
import com.jasik.momsnaggingapi.domain.schedule.Category.CategoryResponse;
import com.jasik.momsnaggingapi.domain.schedule.Schedule;
import com.jasik.momsnaggingapi.domain.schedule.Schedule.ArrayListRequest;
import com.jasik.momsnaggingapi.domain.schedule.Schedule.CategoryListResponse;
import com.jasik.momsnaggingapi.domain.schedule.Schedule.ScheduleListResponse;
import com.jasik.momsnaggingapi.domain.schedule.Schedule.ScheduleType;
import com.jasik.momsnaggingapi.domain.schedule.repository.CategoryRepository;
import com.jasik.momsnaggingapi.domain.schedule.repository.ScheduleRepository;
import com.jasik.momsnaggingapi.domain.user.User;
import com.jasik.momsnaggingapi.domain.user.repository.UserRepository;
import com.jasik.momsnaggingapi.infra.common.AsyncService;
import com.jasik.momsnaggingapi.infra.common.ErrorCode;
import com.jasik.momsnaggingapi.infra.common.exception.ScheduleNotFoundException;
import com.jasik.momsnaggingapi.infra.common.exception.ThreadFullException;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.RejectedExecutionException;
import java.util.stream.Collectors;
import javax.json.JsonPatch;
import javax.json.JsonStructure;
import javax.json.JsonValue;
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
@RequiredArgsConstructor
public class ScheduleService extends RejectedExecutionException {

    private final ScheduleRepository scheduleRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final ObjectMapper objectMapper;
    private final AsyncService asyncService;

    @Transactional
    public Schedule.ScheduleResponse postSchedule(Long userId, Schedule.ScheduleRequest dto) {
        // TODO: nagging ID 연동
        // TODO: 하루 최대 생성갯수 조건 추가
        // 커스텀 할일/습관일 경우 nagging 지정
        if (dto.getNaggingId() == null || dto.getNaggingId() == 0) {
            dto.setNaggingId(1L);
        }
        Schedule newSchedule = modelMapper.map(dto, Schedule.class);
        Schedule originSchedule = scheduleRepository.save(newSchedule);
        // TODO : 생성 -> 업데이트 로직 개선사항 찾기 -> select last_insert_id()
        originSchedule.initOriginalId();
        originSchedule.initScheduleTypeAndUserId(userId);
        originSchedule.verifyRoutine();
        originSchedule = scheduleRepository.save(originSchedule);
        // 습관 스케줄 저장 로직(n회 습관은 제외)
        if (originSchedule.getScheduleType() == Schedule.ScheduleType.ROUTINE) {

            addRoutineOrder(userId, originSchedule.getId());
            if (originSchedule.getGoalCount() == 0) {
                try {
                    Schedule finalOriginSchedule = originSchedule;
                    asyncService.run(() -> createRoutine(finalOriginSchedule));
                } catch (RejectedExecutionException e) {
                    throw new ThreadFullException("Async Thread was fulled", ErrorCode.THREAD_FULL);
                }
            }
        }
        return modelMapper.map(originSchedule, Schedule.ScheduleResponse.class);
    }

    private void addRoutineOrder(Long userId, Long scheduleId) {
        //            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findById(userId)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다."));
        List<String> orderList;
        Optional<List<String>> optionalList = Optional.ofNullable(user.getRoutineOrder());
        if (optionalList.isPresent()) {
            orderList = optionalList.get();
            orderList.add(String.valueOf(scheduleId));
        } else {
            orderList = Collections.singletonList(
                String.valueOf(scheduleId));
        }
        user.updateRoutineOrder(orderList);
        userRepository.save(user);
    }

    private void createRoutine(Schedule originSchedule) {

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
                Schedule nextSchedule = Schedule.builder().build();
                BeanUtils.copyProperties(originSchedule, nextSchedule, "id", "scheduleDate");
                nextSchedule.initScheduleDate(nextScheduleDate);
                nextSchedules.add(nextSchedule);
            }
            weekCount += 1;
        }
        scheduleRepository.saveAll(nextSchedules);
    }

    @Transactional(readOnly = true)
    public List<ScheduleListResponse> getSchedules(Long userId, LocalDate scheduleDate) {

        // TODO: routineOrder에 맞춰서 반환
//            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findById(userId)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다."));
        List<String> routineOrder = user.getRoutineOrder();

        // 전체 id 순으로 정렬
        List<Schedule> schedules = scheduleRepository.findAllByScheduleDateAndUserIdOrderByIdAsc(
            scheduleDate, userId);

        // 습관만 정렬 수정
        boolean isFirst = true;
        int index = 0;
        Map<Integer, Schedule> todoSchedules = new HashMap<>();
        ArrayList<Schedule> routineSchedules = new ArrayList<>();
        for (String scheduleId : routineOrder) {
            for (Schedule schedule : schedules) {
                if (Objects.equals(String.valueOf(schedule.getOriginalId()), scheduleId)) {
                    routineSchedules.add(schedule);
                }
                // 할일 순서 기억
                if (isFirst && (schedule.getScheduleType() == ScheduleType.TODO)) {
                    todoSchedules.put(index, schedule);
                }
                index++;
            }
            isFirst = false;
        }
        // 할일은 원래 순서에 두기
        for (Entry<Integer, Schedule> todoMap : todoSchedules.entrySet()) {
            routineSchedules.add(todoMap.getKey(), todoMap.getValue());
        }

        return routineSchedules.stream().map(Schedule -> modelMapper.map(Schedule,
                com.jasik.momsnaggingapi.domain.schedule.Schedule.ScheduleListResponse.class))
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Schedule.ScheduleResponse getSchedule(Long scheduleId) {

        return scheduleRepository.findById(scheduleId)
            .map(value -> modelMapper.map(value, Schedule.ScheduleResponse.class)).orElseThrow(
                () -> new ScheduleNotFoundException("schedule was not found",
                    ErrorCode.SCHEDULE_NOT_FOUND));
    }

    @Transactional
    public Schedule.ScheduleResponse patchSchedule(Long userId, Long scheduleId, JsonPatch jsonPatch) {

        Schedule targetSchedule = scheduleRepository.findByIdAndUserId(scheduleId, userId)
            .orElseThrow(() -> new ScheduleNotFoundException("schedule was not found",
                ErrorCode.SCHEDULE_NOT_FOUND));
        boolean beforeDone = targetSchedule.getDone();
        // 타겟 스케줄 변경사항 적용
        Schedule modifiedSchedule = scheduleRepository.save(
            mergeSchedule(targetSchedule, jsonPatch));
        ArrayList<String> columnList = new ArrayList<>();
        for (JsonValue i : jsonPatch.toJsonArray()) {
            columnList.add(String.valueOf(i.asJsonObject().get("path")).replaceAll("\"", "")
                .replaceAll("/", ""));
        }
        // n회 반복 습관의 수행 완료 처리인 경우
        if (columnList.contains("done") && (
            modifiedSchedule.getScheduleType() == ScheduleType.ROUTINE) && (
            modifiedSchedule.getGoalCount() > 0)) {
            Schedule originSchedule = scheduleRepository.findByIdAndUserId(
                modifiedSchedule.getOriginalId(), userId).orElseThrow(
                () -> new ScheduleNotFoundException("schedule was not found",
                    ErrorCode.SCHEDULE_NOT_FOUND));
            // true -> false
            if (beforeDone && (!modifiedSchedule.getDone())) {
                originSchedule.minusDoneCount();
            }
            // false -> true
            else if ((!beforeDone) && (modifiedSchedule.getDone())) {
                // 목표 미완 and 내일 주차 == 원본 주차 =-> 다음날 한개 더 생성
                if (!originSchedule.plusDoneCount()
                    && (
                    modifiedSchedule.getScheduleDate().plusDays(1).get(WeekFields.ISO.weekOfYear())
                        == originSchedule.getScheduleDate().get(WeekFields.ISO.weekOfYear()))
                ) {
                    Schedule nextSchedule = Schedule.builder().build();
                    BeanUtils.copyProperties(modifiedSchedule, nextSchedule, "id", "doneCount");
                    nextSchedule.initNextSchedule();
                    Optional<Schedule> optionalSchedule = scheduleRepository.findByUserIdAndOriginalIdAndScheduleDate(
                        userId,
                        nextSchedule.getOriginalId(), nextSchedule.getScheduleDate());
                    // 완료 -> 미완료 -> 완료 케이스에 대한 처리 추가 ==> 다음날에 같은 스케줄 있으면 미생성
                    if (!optionalSchedule.isPresent()) {
                        scheduleRepository.save(nextSchedule);
                    }
                }
            }
            scheduleRepository.save(originSchedule);
        }
        // 요일 반복 옵션 수정이 포함된 경우 삭제 후 재 생성
        if (columnList.contains("mon") || columnList.contains("tue") || columnList.contains("wed")
            || columnList.contains("thu") || columnList.contains("fri") || columnList.contains(
            "sat") || columnList.contains("sun")) {
            scheduleRepository.deleteWithIdAfter(modifiedSchedule.getId(),
                modifiedSchedule.getUserId(), modifiedSchedule.getOriginalId());
            modifiedSchedule.initOriginalId();
            scheduleRepository.save(modifiedSchedule);
            addRoutineOrder(userId, modifiedSchedule.getId());
            try {
                asyncService.run(() -> createRoutine(modifiedSchedule));
            } catch (RejectedExecutionException e) {
                throw new ThreadFullException("Async Thread was fulled", ErrorCode.THREAD_FULL);
            }
        }
        // n회 반복 옵션이 수정된 경우 -> 원본이 같은 n회 습관들 모두 업데이트
        else if (columnList.contains("goalCount")) {
            scheduleRepository.updateNRoutineWithUserIdAndOriginalId(
                modifiedSchedule.getGoalCount(), modifiedSchedule.getScheduleName(),
                modifiedSchedule.getScheduleTime(), modifiedSchedule.getAlarmTime(),
                modifiedSchedule.getUserId(), modifiedSchedule.getOriginalId());
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
    }

    private Schedule mergeSchedule(Schedule originalSchedule, JsonPatch jsonPatch) {

        JsonStructure target = objectMapper.convertValue(originalSchedule, JsonStructure.class);
        JsonValue patchedSchedule = jsonPatch.apply(target);

        return objectMapper.convertValue(patchedSchedule, Schedule.class);
    }

    @Transactional
    public void deleteSchedule(Long userId, Long scheduleId) {

        Schedule schedule = scheduleRepository.findByIdAndUserId(scheduleId, userId).orElseThrow(
            () -> new ScheduleNotFoundException("schedule was not found",
                ErrorCode.SCHEDULE_NOT_FOUND));
        // n회 습관인 경우 원본의 goalCount를 0으로 해야 다음 주차에 생성 안됨
        if (schedule.getGoalCount() > 0) {
            Schedule originSchedule = scheduleRepository.findByIdAndUserId(schedule.getOriginalId(),
                userId).orElseThrow(() -> new ScheduleNotFoundException("schedule was not found",
                ErrorCode.SCHEDULE_NOT_FOUND));
            originSchedule.initGoalCount();
            scheduleRepository.save(originSchedule);
        }
        scheduleRepository.deleteWithIdAfter(scheduleId, userId, schedule.getOriginalId());
    }

    @Transactional
    public void postSchedulesArray(Long userId, ArrayList<ArrayListRequest> arrayRequest) {

//            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findById(userId)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다."));
        List<String> routineOrder = user.getRoutineOrder();
        for (ArrayListRequest changedMap : arrayRequest) {
            int oneIndex = routineOrder.indexOf(String.valueOf(changedMap.getOneOriginalId()));
            int theOtherIndex = routineOrder.indexOf(
                String.valueOf(changedMap.getTheOtherOriginalId()));
            if ((oneIndex == -1) || (theOtherIndex == -1)) {
                throw new ScheduleNotFoundException("schedule was not found",
                    ErrorCode.SCHEDULE_NOT_FOUND);
            }
            Collections.swap(routineOrder, oneIndex, theOtherIndex);
        }
        user.updateRoutineOrder(routineOrder);
        userRepository.save(user);
    }

//    @Transactional
//    public Category.CategoryResponse postCategory(Category.CategoryRequest dto) {
//
//        Optional<Category> nullCategory = categoryRepository.findByCategoryName(
//            dto.getCategoryName());
//
//        if (nullCategory.isPresent()) {
//            return null;
//        }
//
//        Long userId = 1L;
//        Category category = modelMapper.map(dto, Category.class);
//        category.initUserId(userId);
//        Category newCategory = categoryRepository.save(category);
//
//        return modelMapper.map(newCategory, Category.CategoryResponse.class);
//    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> getCategories() {

        List<Category> categories = categoryRepository.findAllByUsed(true);

        return categories.stream()
            .map(Category -> modelMapper.map(Category, CategoryResponse.class))
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CategoryListResponse> getCategorySchedules(Long categoryId) {

        List<Schedule> schedules = scheduleRepository.findAllByCategoryId(categoryId);

        return schedules.stream()
            .map(Schedule -> modelMapper.map(Schedule, CategoryListResponse.class))
            .collect(Collectors.toList());
    }
}