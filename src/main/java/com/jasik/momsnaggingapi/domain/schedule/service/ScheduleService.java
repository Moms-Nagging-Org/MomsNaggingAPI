package com.jasik.momsnaggingapi.domain.schedule.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jasik.momsnaggingapi.domain.schedule.Category;
import com.jasik.momsnaggingapi.domain.schedule.Category.CategoryResponse;
import com.jasik.momsnaggingapi.domain.schedule.Interface.ScheduleNaggingInterface;
import com.jasik.momsnaggingapi.domain.schedule.Schedule;
import com.jasik.momsnaggingapi.domain.schedule.Schedule.ArrayListRequest;
import com.jasik.momsnaggingapi.domain.schedule.Schedule.CategoryListResponse;
import com.jasik.momsnaggingapi.domain.schedule.Schedule.ScheduleListResponse;
import com.jasik.momsnaggingapi.domain.schedule.Schedule.ScheduleResponse;
import com.jasik.momsnaggingapi.domain.schedule.repository.CategoryRepository;
import com.jasik.momsnaggingapi.domain.schedule.repository.ScheduleRepository;
import com.jasik.momsnaggingapi.domain.user.User;
import com.jasik.momsnaggingapi.domain.user.repository.UserRepository;
import com.jasik.momsnaggingapi.infra.common.AsyncService;
import com.jasik.momsnaggingapi.infra.common.ErrorCode;
import com.jasik.momsnaggingapi.infra.common.Utils;
import com.jasik.momsnaggingapi.infra.common.exception.NotValidStatusException;
import com.jasik.momsnaggingapi.infra.common.exception.ScheduleNotFoundException;
import com.jasik.momsnaggingapi.infra.common.exception.ThreadFullException;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
    private final Utils utils;

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
        addRoutineOrder(userId, originSchedule.getId());

        if (originSchedule.getScheduleType() == Schedule.ScheduleType.ROUTINE) {
            try {
                Schedule finalOriginSchedule = originSchedule;
                asyncService.run(() -> createRoutine(finalOriginSchedule));
            } catch (RejectedExecutionException e) {
                throw new ThreadFullException("Async Thread was fulled", ErrorCode.THREAD_FULL);
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
        ArrayList<Schedule> nextSchedules;
        if (originSchedule.getGoalCount() == 0) {
            nextSchedules = getDayOfWeekRepeatSchedules(originSchedule);
        }
        else{
            nextSchedules = getNumberRepeatSchedules(originSchedule);
        }
        scheduleRepository.saveAll(nextSchedules);
    }

    private ArrayList<Schedule> getDayOfWeekRepeatSchedules(Schedule originSchedule) {
        ArrayList<Integer> nextRoutineDays = originSchedule.getNextRoutineDays();
        ArrayList<Schedule> nextSchedules = new ArrayList<>();
        int weekCount = 0;
        boolean limitDateFlag = true;
        LocalDate originScheduleDate = originSchedule.getScheduleDate();
        while (limitDateFlag) {
            // 7일씩 더해야 함
            int nextWeek = 7 * weekCount;
            // 반복 요일마다 주차 더함
            for (int i : nextRoutineDays) {
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
        return nextSchedules;
    }
    private ArrayList<Schedule> getNumberRepeatSchedules(Schedule originSchedule) {
        LocalDate endOfWeek = LocalDate.now().with(DayOfWeek.SUNDAY);
        LocalDateTime startDate = originSchedule.getScheduleDate().atStartOfDay();
        LocalDateTime endDate = endOfWeek.atStartOfDay();
        int betweenDays = (int) Duration.between(startDate, endDate).toDays();

        ArrayList<Schedule> nextSchedules = new ArrayList<>();
        for (int i = 1; i <= betweenDays; i ++) {
            Schedule nextSchedule = Schedule.builder().build();
            LocalDate nextScheduleDate = originSchedule.getScheduleDate().plusDays(i);
            BeanUtils.copyProperties(originSchedule, nextSchedule, "id", "scheduleDate");
            nextSchedule.initScheduleDate(nextScheduleDate);
            nextSchedules.add(nextSchedule);
        }
        return nextSchedules;
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

        if (routineOrder == null) {
            return schedules.stream().map(Schedule -> modelMapper.map(Schedule,
                    com.jasik.momsnaggingapi.domain.schedule.Schedule.ScheduleListResponse.class))
                .collect(Collectors.toList());
        } else {
            return getScheduleListByOrder(routineOrder, schedules).stream().map(Schedule -> modelMapper.map(Schedule,
                ScheduleListResponse.class))
                .collect(Collectors.toList());
        }
    }

    private ArrayList<Schedule> getScheduleListByOrder(List<String> scheduleOrder, List<Schedule> schedules) {
        ArrayList<Schedule> newScheduleArray = new ArrayList<>();
        for (String scheduleId : scheduleOrder) {
            for (Schedule schedule : schedules) {
                if (Objects.equals(String.valueOf(schedule.getOriginalId()), scheduleId)) {
                    newScheduleArray.add(schedule);
                }
            }
        }
        return newScheduleArray;
    }

    @Transactional(readOnly = true)
    public Schedule.ScheduleResponse getSchedule(Long scheduleId) {

        return scheduleRepository.findById(scheduleId)
            .map(value -> modelMapper.map(value, ScheduleResponse.class)).orElseThrow(
                () -> new ScheduleNotFoundException("schedule was not found",
                    ErrorCode.SCHEDULE_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public int getRemainSkipDays(Long scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
            .orElseThrow(() -> new ScheduleNotFoundException("schedule was not found",
            ErrorCode.SCHEDULE_NOT_FOUND));
        Schedule originalSchedule = scheduleRepository.findById(schedule.getOriginalId())
            .orElseThrow(() -> new ScheduleNotFoundException("schedule was not found",
                ErrorCode.SCHEDULE_NOT_FOUND));
        return originalSchedule.getRemainSkipDays(schedule.getScheduleDate().getDayOfWeek().getValue());
    }
    @Transactional
    public Schedule.ScheduleResponse patchSchedule(Long userId, Long scheduleId, JsonPatch jsonPatch) {

        Schedule targetSchedule = scheduleRepository.findByIdAndUserId(scheduleId, userId)
            .orElseThrow(() -> new ScheduleNotFoundException("schedule was not found",
                ErrorCode.SCHEDULE_NOT_FOUND));
        int beforeStatus = targetSchedule.getStatus();
        Schedule modifiedSchedule = scheduleRepository.save(
            mergeSchedule(targetSchedule, jsonPatch));
        ArrayList<String> columnList = new ArrayList<>();
        for (JsonValue i : jsonPatch.toJsonArray()) {
            columnList.add(String.valueOf(i.asJsonObject().get("path")).replaceAll("\"", "")
                .replaceAll("/", ""));
        }
        // n회 반복 습관의 상태 변경인 경우
        // 미룸 -> 수요일(3)의 습관을 미룸 상태로 변경할 경우, 목표 카운트(5) > 주차의 남은 일 수(7-3=4) 이고 목표 미달성일 때 에러
        if (columnList.contains("status") && (
            modifiedSchedule.checkNumberRepeatSchedule())) {
            Schedule originSchedule = scheduleRepository.findByIdAndUserId(
                modifiedSchedule.getOriginalId(), userId).orElseThrow(
                () -> new ScheduleNotFoundException("schedule was not found",
                    ErrorCode.SCHEDULE_NOT_FOUND));
            if (modifiedSchedule.getStatus() == 1) {
                if (beforeStatus != 1){
                    originSchedule.plusDoneCount();
                }
            }
            else{
                if (beforeStatus == 1){
                    originSchedule.minusDoneCount();
                }
                // TODO: 이후 날짜뿐만 아니라 이전 날짜에서 status 2로 변경 시에도 막아야 함
                if (modifiedSchedule.getStatus() == 2) {
                    int modified_date = modifiedSchedule.getScheduleDate().getDayOfWeek().getValue();
                    int remain_days = 7 - modified_date;
                    if ((originSchedule.getGoalCount() - originSchedule.getDoneCount() > remain_days) && (
                        !originSchedule.achievedGoalCount())) {
                            throw new NotValidStatusException("You can't postpone your schedule.", ErrorCode.NOT_VALID_STATUS);
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
        if ((schedule.getGoalCount() > 0) && (!Objects.equals(schedule.getId(),
            schedule.getOriginalId()))) {
            Optional<Schedule> optionalSchedule = scheduleRepository.findByIdAndUserId(schedule.getOriginalId(), userId);
            if (optionalSchedule.isPresent()) {
                Schedule originSchedule = optionalSchedule.get();
                originSchedule.initGoalCount();
                if (schedule.getStatus() == 1) {
                    originSchedule.minusDoneCount();
                }
                scheduleRepository.save(originSchedule);
            }
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

    @Transactional(readOnly = true)
    public List<Schedule.CategoryListAdminResponse> getTemplateSchedulesByCategory(Long categoryId) {

        List<ScheduleNaggingInterface> schedules = scheduleRepository.findDetailsAllByCategoryId(categoryId);

        return schedules.stream()
                .map(s -> new Schedule.CategoryListAdminResponse(
                        s.getSchedule().getId(),
                        s.getSchedule().getScheduleName(),
                        s.getNagging().getLevel1(),
                        s.getNagging().getLevel2(),
                        s.getNagging().getLevel3()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void createNumberRepeatSchedulesOfNewWeek(Long userId) {
        try {
            LocalDate weekAgoDate = LocalDate.now().minusDays(7);
            List<String> daysOfWeek = utils.getDaysOfWeek(weekAgoDate);
            LocalDate startDate = LocalDate.parse(daysOfWeek.get(0), DateTimeFormatter.ISO_DATE);
            LocalDate endDate = LocalDate.parse(daysOfWeek.get(1), DateTimeFormatter.ISO_DATE);
            asyncService.run(()->createNRoutines(userId, startDate, endDate));
        } catch (RejectedExecutionException e) {
            throw new ThreadFullException("Async Thread was fulled", ErrorCode.THREAD_FULL);
        }
    }

    public void createNRoutines(Long userId, LocalDate startDate, LocalDate endDate) {
        List<Schedule> nRoutineSchedules = scheduleRepository.findAllByUserIdAndGoalCountGreaterThanAndScheduleDateGreaterThanEqualAndScheduleDateLessThanEqual(
            userId, 0, startDate, endDate);
        ArrayList<Schedule> newSchedules = new ArrayList<>();
        for (Schedule schedule : nRoutineSchedules) {
            if (schedule.checkOriginalSchedule()) {
                newSchedules.addAll(getNumberRepeatSchedules(copyNewOriginSchedule(schedule, "id", "scheduleDate")));
            }
        }
        scheduleRepository.saveAll(newSchedules);
    }

    public Schedule copyNewOriginSchedule(Schedule schedule, String... ignoreProperties) {
        Schedule newSchedule = Schedule.builder().build();
        BeanUtils.copyProperties(schedule, newSchedule, ignoreProperties);
        newSchedule.initScheduleDate(LocalDate.now());
        newSchedule = scheduleRepository.save(newSchedule);
        newSchedule.initOriginalId();
        return newSchedule;
    }

}