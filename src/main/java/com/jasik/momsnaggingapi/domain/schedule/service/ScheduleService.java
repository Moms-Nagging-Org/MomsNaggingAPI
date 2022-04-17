package com.jasik.momsnaggingapi.domain.schedule.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jasik.momsnaggingapi.domain.schedule.Schedule;
import com.jasik.momsnaggingapi.domain.schedule.Schedule.ScheduleRequest;
import com.jasik.momsnaggingapi.domain.schedule.Schedule.ScheduleResponse;
import com.jasik.momsnaggingapi.domain.schedule.Schedule.SchedulesResponse;
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
    public Collection<SchedulesResponse> getSchedules() {

//        log.error("test error");
//        log.info("test info");
        Collection<SchedulesResponse> scheduleAllResponses = new ArrayList<SchedulesResponse>();
        scheduleAllResponses.add(new SchedulesResponse());
//        Collection<Schedule> schedules = scheduleRepository.findAllByScheduleDateAndUserId(scheduleDate, userId);
////
//        schedules.forEach(schedule ->
//                scheduleAllResponses.add(new AllResponse(
//                        schedule.getId(),
//                        schedule.getSeqNumber(),
//                        schedule.getTitle(),
//                        schedule.getScheduleTime(),
//                        schedule.,
//                        schedule.getScheduleType()
//                        )));

        return scheduleAllResponses;
    }

    @Transactional
    public ScheduleResponse postSchedule(ScheduleRequest schedule) {

        ScheduleResponse new_schedule = new ScheduleResponse();

        return new_schedule;
    }

    @Transactional(readOnly = true)
    public ScheduleResponse getSchedule(Long scheduleId) {

        ScheduleResponse scheduleResponse = new ScheduleResponse();

        return scheduleResponse;
    }

    @Transactional
    public ScheduleResponse putSchedule(Long scheduleId, JsonPatch jsonPatch) {

        Optional<Schedule> originalSchedule = scheduleRepository.findById(scheduleId);
        Schedule modifiedSchedule = mergeSchedule(originalSchedule, jsonPatch); //패치처리
        scheduleRepository.save(modifiedSchedule);
        ScheduleResponse scheduleResponse = modelMapper.map(originalSchedule, ScheduleResponse.class);

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
}