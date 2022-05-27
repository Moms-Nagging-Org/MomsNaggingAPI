package com.jasik.momsnaggingapi.domain.schedule.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jasik.momsnaggingapi.infra.common.AsyncService;
import com.jasik.momsnaggingapi.domain.schedule.Category;
import com.jasik.momsnaggingapi.domain.schedule.Category.CategoryResponse;
import com.jasik.momsnaggingapi.domain.schedule.Schedule;
import com.jasik.momsnaggingapi.domain.schedule.Schedule.CategoryListResponse;
import com.jasik.momsnaggingapi.domain.schedule.Schedule.ScheduleListResponse;
import com.jasik.momsnaggingapi.domain.schedule.Schedule.ScheduleType;
import com.jasik.momsnaggingapi.domain.schedule.repository.CategoryRepository;
import com.jasik.momsnaggingapi.domain.schedule.repository.ScheduleRepository;
import com.jasik.momsnaggingapi.infra.common.ErrorCode;
import com.jasik.momsnaggingapi.infra.common.exception.ScheduleNotFoundException;
import com.jasik.momsnaggingapi.infra.common.exception.ThreadFullException;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.RejectedExecutionException;
import java.util.stream.Collectors;
import javax.json.JsonPatch;
import javax.json.JsonStructure;
import javax.json.JsonValue;
import jdk.vm.ci.meta.Local;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduleService extends RejectedExecutionException {

    private final ScheduleRepository scheduleRepository;
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;
    private final ObjectMapper objectMapper;
    private final AsyncService asyncService;

    private int getNextSeqNumber(Long userId, LocalDate scheduleDate) {
        Optional<Schedule> optionalLastSchedule = scheduleRepository.findFirstByUserIdAndScheduleDateOrderBySeqNumberDesc(
            userId, scheduleDate);
        return optionalLastSchedule.map(schedule -> schedule.getSeqNumber() + 1).orElse(0);
    }

    @Transactional
    public Schedule.ScheduleResponse postSchedule(Schedule.ScheduleRequest dto) {

        // TODO: nagging ID 연동
        Long userId = 1L;
        // TODO: 하루 최대 생성갯수 조건 추가
        if (dto.getNaggingId() != null && dto.getNaggingId() == 0) {
            dto.setNaggingId(null);
        }
        Schedule newSchedule = modelMapper.map(dto, Schedule.class);
        // TODO: TODO/ROUTINE에 따른 SeqNumber 설정
        newSchedule.initSeqNumber(getNextSeqNumber(userId, dto.getScheduleDate()));
        Schedule originSchedule = scheduleRepository.save(newSchedule);
        // TODO : 생성 -> 업데이트 로직 개선사항 찾기 -> select last_insert_id()
        // seqNumber 마지막 번호로 추가
        originSchedule.initOriginalId();
        originSchedule.initScheduleTypeAndUserId(userId);
        originSchedule.verifyRoutine();
        originSchedule = scheduleRepository.save(originSchedule);
        // 습관 스케줄 저장 로직(n회 습관은 제외)
        if (originSchedule.getScheduleType() == Schedule.ScheduleType.ROUTINE
            && originSchedule.getGoalCount() == 0) {
            try {
                Schedule finalOriginSchedule = originSchedule;
                asyncService.run(() -> createRoutine(finalOriginSchedule));
            } catch (RejectedExecutionException e) {
                throw new ThreadFullException("Async Thread was fulled", ErrorCode.THREAD_FULL);
            }
        }
        return modelMapper.map(originSchedule, Schedule.ScheduleResponse.class);
    }

    public void createRoutine(Schedule originSchedule) {

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
                nextSchedule.initSeqNumber(
                    getNextSeqNumber(nextSchedule.getUserId(), nextScheduleDate));
                nextSchedules.add(nextSchedule);
            }
            weekCount += 1;
        }
        scheduleRepository.saveAll(nextSchedules);
    }

    @Transactional(readOnly = true)
    public List<ScheduleListResponse> getSchedules(LocalDate scheduleDate) {

        Long userId = 1L;

        List<Schedule> schedules = scheduleRepository.findAllByScheduleDateAndUserIdOrderBySeqNumberAsc(
            scheduleDate, userId);

        return schedules.stream().map(Schedule -> modelMapper.map(Schedule,
                com.jasik.momsnaggingapi.domain.schedule.Schedule.ScheduleListResponse.class))
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Schedule.ScheduleResponse getSchedule(Long scheduleId) {
        Long userId = 1L;

        return scheduleRepository.findByIdAndUserId(scheduleId, userId)
            .map(value -> modelMapper.map(value, Schedule.ScheduleResponse.class)).orElseThrow(
                () -> new ScheduleNotFoundException("schedule was not found",
                    ErrorCode.SCHEDULE_NOT_FOUND));
    }

    @Transactional
    public Schedule.ScheduleResponse patchSchedule(Long scheduleId, JsonPatch jsonPatch) {

        Long userId = 1L;

        Schedule targetSchedule = scheduleRepository.findByIdAndUserId(scheduleId, userId)
            .orElseThrow(() -> new ScheduleNotFoundException("schedule was not found",
                ErrorCode.SCHEDULE_NOT_FOUND));
        // 타겟 스케줄 변경사항 적용
        Schedule modifiedSchedule = scheduleRepository.save(
            mergeSchedule(targetSchedule, jsonPatch));
        ArrayList<String> columnList = new ArrayList<>();
        for (JsonValue i : jsonPatch.toJsonArray()) {
            columnList.add(String.valueOf(i.asJsonObject().get("path")).replaceAll("\"", "")
                .replaceAll("/", ""));
        }
        // n회 반복 습관의 수행 완료 처리인 경우
        if (columnList.contains("/done") && modifiedSchedule.getDone() && (
            modifiedSchedule.getScheduleType() == ScheduleType.ROUTINE) && (
            modifiedSchedule.getGoalCount() > 0)) {
            Schedule originSchedule = scheduleRepository.findByIdAndUserId(
                modifiedSchedule.getOriginalId(), userId).orElseThrow(
                () -> new ScheduleNotFoundException("schedule was not found",
                    ErrorCode.SCHEDULE_NOT_FOUND));
            // 목표 미완 and 내일 주차 == 원본 주차 =-> 다음날 한개 더 생성
            if (!originSchedule.plusDoneCount()
                && modifiedSchedule.getScheduleDate().plusDays(1).get(WeekFields.ISO.weekOfYear())
                == originSchedule.getScheduleDate().get(WeekFields.ISO.weekOfYear())) {
                modifiedSchedule.initNextSchedule();
                modifiedSchedule.initSeqNumber(
                    getNextSeqNumber(modifiedSchedule.getUserId(), modifiedSchedule.getScheduleDate()));
                scheduleRepository.save(modifiedSchedule);
            }
        }
        // 요일 반복 옵션 수정이 포함된 경우 삭제 후 재 생성
        if (columnList.contains("mon") || columnList.contains("tue") || columnList.contains("wed")
            || columnList.contains("thu") || columnList.contains("fri") || columnList.contains(
            "sat") || columnList.contains("sun")) {
            scheduleRepository.deleteWithIdAfter(modifiedSchedule.getId(),
                modifiedSchedule.getUserId(), modifiedSchedule.getOriginalId());
            modifiedSchedule.initOriginalId();
            scheduleRepository.save(modifiedSchedule);
            try {
                asyncService.run(() -> createRoutine(modifiedSchedule));
            } catch (RejectedExecutionException e) {
                throw new ThreadFullException("Async Thread was fulled", ErrorCode.THREAD_FULL);
            }
        }
        // TODO: n회 습관 수정사항을 원본에도 적용으로 변경 필요
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
    public void deleteSchedule(Long scheduleId) {

        Long userId = 1L;
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
//
//    @Transactional
//    public List<ScheduleListResponse> postSchedulesArray(ArrayList<Long> arrayRequest, LocalDate scheduleDate) {
//        // originSchedules = 원본 스케줄 리스트(원본 id)
//        // beforeSchedules = 수정된 스케줄의 수정 전 순서 리스트
//        // afterSchedules = 수정된 스케줄의 수정 후 순서 리스트
//
//        // nextOriginSchedules = 다음 날의 원본 스케줄 리스트(원본 Id)
//        // afterSeq = 다음 날의 수정 대상 스케줄의 수정 전 순서 리스트
//        // changeScheduleIds = 다음 날의 수정 대상 스케줄의 수정 후 순서 리스트
//        // nextChangedSchedules = 다음 날의 수정 완료된 순서 리스트
//        Long userId = 1L;
//        ArrayList<Long> beforeIds = new ArrayList<>();
//        ArrayList<Long> afterIds = new ArrayList<>();
//        ArrayList<Long> originSchedules = scheduleRepository.getOriginIdByUserIdAndScheduleDateOrderBySeqNumberAsc(
//            userId,
//            scheduleDate);
//        for (int i = 0; i <= originSchedules.size(); i++) {
//            // 원본 스케줄 리스트와 수정 후 리스트가 다르면 수정 전 리스트에 추가
//            Long beforeId = originSchedules.get(i);
//            Long afterId = arrayRequest.get(i);
//            if (!Objects.equals(beforeId, afterId)) {
//                beforeIds.add(beforeId);
//                afterIds.add(afterId);
//            }
//        }
//
//        // 다음 날부터 쭉 비교, 마지막 스케줄 날짜까지
//        ArrayList<Schedule> nextOriginSchedules = scheduleRepository.findAllByUserIdAndScheduleDateAfter(userId, scheduleDate);
//        ArrayList<Schedule> dailySchedules = new ArrayList<>();
//        LocalDate beforeDate = scheduleDate;
//        LocalDate afterDate;
//        for (Schedule scheduleOfAll : nextOriginSchedules) {
//            afterDate = scheduleOfAll.getScheduleDate();
//            if (afterDate.isAfter(beforeDate)) {
//                // 하루 단위에 대한 로직
//                int idx = 0;
//                ArrayList<Integer> indexOfBefore = new ArrayList<>();
//                ArrayList<Integer> indexOfAfter = new ArrayList<>();
//                // 변경 전, 후 스케줄 순서 저장
//                for (Schedule scheduleOfDay : dailySchedules) {
//                    for (Long beforeId : beforeIds) {
//                        if (Objects.equals(scheduleOfDay.getOriginalId(), beforeId)) {
//                            indexOfBefore.add(idx);
//                        }
//                    }
//                    for (Long afterId : afterIds) {
//                        if (Objects.equals(scheduleOfDay.getOriginalId(), afterId)) {
//                            indexOfAfter.add(idx);
//                        }
//                    }
//                    idx++;
//                }
//                // 바뀔 스케줄
//                idx = 0;
//                for (Schedule scheduleOfDay : dailySchedules) {
//                    if(idx )
//                    for (int afterIdx : indexOfAfter) {
//                    dailySchedules.get(afterIdx).set;
//                    idx++;
//                }
//
//                dailySchedules.clear();
//                beforeDate = afterDate;
//            }
//            dailySchedules.add(scheduleOfAll);
//        }
//
//            // 전체 조회 -> 전체 저장
//            ArrayList<Integer> afterSeq = new ArrayList<>();
//            ArrayList<Integer> changeScheduleIds = new ArrayList<>();
//
//            //
//
//            List<ScheduleListResponse> scheduleAllResponses = new ArrayList<>();
//            scheduleAllResponses.add(new ScheduleListResponse());
//
//            return scheduleAllResponses;
//        }
//    }

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