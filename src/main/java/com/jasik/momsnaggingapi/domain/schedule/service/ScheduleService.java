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
import org.springframework.web.server.ResponseStatusException;

import javax.json.JsonPatch;
import javax.json.JsonStructure;
import javax.json.JsonValue;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final ModelMapper modelMapper;
    private final ObjectMapper objectMapper;


    @Transactional
    public Schedule.Response postSchedule(Schedule.Request dto) {
        // TODO : 반복에 따른 습관 추가

        Schedule saveSchedule = modelMapper.map(dto, Schedule.class);
        Schedule schedule = scheduleRepository.save(saveSchedule);
        return modelMapper.map(schedule, Schedule.Response.class);
    }

    @Transactional(readOnly = true)
    public List<Schedule.ListResponse> getSchedules(Long userId, LocalDate scheduleDate) {

//        log.error("test error");
//        log.info("test info");
        List<Schedule> schedules = scheduleRepository.findAllByScheduleDateAndUserId(scheduleDate, userId);
        List<Schedule.ListResponse> resultList = schedules.stream()
                                                        .map(Schedule -> modelMapper.map(Schedule, Schedule.ListResponse.class))
                                                        .collect(Collectors.toList());
//        List<Schedule.ListResponse> resultList = Arrays.asList(modelMapper.map(Schedule, Schedule.ListResponse.class));

        return resultList;
    }

    @Transactional(readOnly = true)
    public Schedule.Response getSchedule(Long scheduleId) {

        Optional<Schedule> schedule = scheduleRepository.findById(scheduleId);
        if (schedule.isPresent()){
            return modelMapper.map(schedule.get(), Schedule.Response.class);
        }
            return null;
    }

    @Transactional
    public Schedule.Response putSchedule(Long scheduleId, JsonPatch jsonPatch) {

        Optional<Schedule> originalSchedule = scheduleRepository.findById(scheduleId);
        Schedule modifiedSchedule = mergeSchedule(originalSchedule, jsonPatch); //패치처리
        scheduleRepository.save(modifiedSchedule);
        Schedule.Response response = modelMapper.map(originalSchedule, Schedule.Response.class);

        return response;
    }

    private Schedule mergeSchedule(Optional<Schedule> originalSchedule, JsonPatch jsonPatch) {

        JsonStructure target = objectMapper.convertValue(originalSchedule, JsonStructure.class);
        JsonValue patchedSchedule = jsonPatch.apply(target);

        return objectMapper.convertValue(patchedSchedule, Schedule.class);
    }

    @Transactional
    public void deleteSchedule(Long scheduleId) {

        // TODO : 삭제 Exception 추가
        scheduleRepository.deleteById(scheduleId);
    }

    @Transactional
    public List<Schedule.ListResponse> postSchedulesArray(List<Long> scheduleArrayRequest) {

        List<Schedule.ListResponse> scheduleAllResponses = new ArrayList<Schedule.ListResponse>();
        scheduleAllResponses.add(new Schedule.ListResponse());

        return scheduleAllResponses;
    }

    @Transactional(readOnly = true)
    public List<Category.Response> getCategories() {

        List<Category.Response> categoriesResponses = new ArrayList<Category.Response>();
        categoriesResponses.add(new Category.Response());

        return categoriesResponses;
    }

    @Transactional(readOnly = true)
    public List<Category.ScheduleResponse> getCategorySchedules(Long categoryId) {

        List<Category.ScheduleResponse> scheduleResponses = new ArrayList<Category.ScheduleResponse>();
        scheduleResponses.add(new Category.ScheduleResponse());

        return scheduleResponses;
    }
}