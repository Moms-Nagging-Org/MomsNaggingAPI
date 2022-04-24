package com.jasik.momsnaggingapi.domain.schedule.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jasik.momsnaggingapi.domain.schedule.Category;
import com.jasik.momsnaggingapi.domain.schedule.Schedule;
import com.jasik.momsnaggingapi.domain.schedule.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.json.JsonPatch;
import javax.json.JsonStructure;
import javax.json.JsonValue;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final ModelMapper modelMapper;
    private final ObjectMapper objectMapper;

    @Transactional(readOnly = true)
    public Collection<Schedule.SchedulesResponse> getSchedules() {

//        log.error("test error");
//        log.info("test info");
        Collection<Schedule.SchedulesResponse> schedulesResponses = new ArrayList<Schedule.SchedulesResponse>();
        schedulesResponses.add(new Schedule.SchedulesResponse());
//        Collection<Schedule> schedules = scheduleRepository.findAllByScheduleDateAndUserId(scheduleDate, userId);
////
//        schedules.forEach(schedule ->
//                scheduleAllResponse.add(new AllResponse(
//                        schedule.getId(),
//                        schedule.getSeqNumber(),
//                        schedule.getTitle(),
//                        schedule.getScheduleTime(),
//                        schedule.,
//                        schedule.getScheduleType()
//                        )));

        return schedulesResponses;
    }

    @Transactional
    public Schedule.ScheduleResponse postSchedule(Schedule.ScheduleRequest schedule) {

        Schedule.ScheduleResponse new_schedule = new Schedule.ScheduleResponse();

        return new_schedule;
    }

    @Transactional(readOnly = true)
    public Schedule.ScheduleResponse getSchedule(Long scheduleId) {

        Schedule.ScheduleResponse scheduleResponse = new Schedule.ScheduleResponse();

        return scheduleResponse;
    }

    @Transactional
    public Schedule.ScheduleResponse putSchedule(Long scheduleId, JsonPatch jsonPatch) {

        Optional<Schedule> originalSchedule = scheduleRepository.findById(scheduleId);
        Schedule modifiedSchedule = mergeSchedule(originalSchedule, jsonPatch); //패치처리
        scheduleRepository.save(modifiedSchedule);
        Schedule.ScheduleResponse scheduleResponse = modelMapper.map(originalSchedule, Schedule.ScheduleResponse.class);

        return scheduleResponse;
    }

    private Schedule mergeSchedule(Optional<Schedule> originalSchedule, JsonPatch jsonPatch) {

        JsonStructure target = objectMapper.convertValue(originalSchedule, JsonStructure.class);
        JsonValue patchedSchedule = jsonPatch.apply(target);

        return objectMapper.convertValue(patchedSchedule, Schedule.class);
    }

    @Transactional
    public void deleteSchedule(Long scheduleId) {

//        scheduleRepository.deleteById(scheduleId);

    }

    @Transactional
    public Collection<Schedule.SchedulesResponse> postSchedulesArray(Collection<Long> scheduleArrayRequest) {

        Collection<Schedule.SchedulesResponse> scheduleAllResponses = new ArrayList<Schedule.SchedulesResponse>();
        scheduleAllResponses.add(new Schedule.SchedulesResponse());

        return scheduleAllResponses;
    }

    @Transactional(readOnly = true)
    public Collection<Category.CategoryResponse> getCategories() {

        Collection<Category.CategoryResponse> categoriesResponses = new ArrayList<Category.CategoryResponse>();
        categoriesResponses.add(new Category.CategoryResponse());

        return categoriesResponses;
    }

    @Transactional(readOnly = true)
    public Collection<Schedule.CategorySchedulesResponse> getCategorySchedules(Long categoryId) {

        Collection<Schedule.CategorySchedulesResponse> categorySchedulesResponses = new ArrayList<Schedule.CategorySchedulesResponse>();
        categorySchedulesResponses.add(new Schedule.CategorySchedulesResponse());

        return categorySchedulesResponses;
    }
}