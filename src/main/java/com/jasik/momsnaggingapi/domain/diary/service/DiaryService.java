package com.jasik.momsnaggingapi.domain.diary.service;

import com.jasik.momsnaggingapi.domain.diary.Diary;
import com.jasik.momsnaggingapi.domain.diary.Diary.DailyResponse;
import com.jasik.momsnaggingapi.domain.diary.Diary.DiaryResponse;
import com.jasik.momsnaggingapi.domain.diary.Diary.DailyDiary;
import com.jasik.momsnaggingapi.domain.diary.repository.DiaryRepository;
import com.jasik.momsnaggingapi.domain.grade.Grade;
import com.jasik.momsnaggingapi.infra.common.Utils;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiaryService {

    private final DiaryRepository diaryRepository;
    private final ModelMapper modelMapper;
    private final Utils utils;

    @Transactional(readOnly = true)
    public Diary.DiaryResponse getDiary(Long userId, LocalDate retrieveDate) {

        // 한국 타임존으로 고정해서 비교
        boolean isToday = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).toLocalDate().isEqual(retrieveDate);

        Optional<Diary> diary = diaryRepository.findByUserIdAndDiaryDate(userId, retrieveDate);
        if (diary.isPresent()) {
            Diary.DiaryResponse responseDto = modelMapper.map(diary.get(),
                Diary.DiaryResponse.class);
            responseDto.setToday(isToday);
            return responseDto;
        } else {
            return new DiaryResponse("", "", retrieveDate, isToday);
        }
    }

    @Transactional()
    public Diary.DiaryResponse putDiary(Long userId, Diary.DiaryRequest requestDto) {

        boolean isToday = LocalDate.now().isEqual(requestDto.getDiaryDate());

        Optional<Diary> optionalDiary = diaryRepository.findByUserIdAndDiaryDate(userId,
            requestDto.getDiaryDate());
        Diary diary;
        if (optionalDiary.isPresent()) {
            diary = optionalDiary.get();
            diary.updateDiary(requestDto.getTitle(), requestDto.getContext());
        } else {
            diary = modelMapper.map(requestDto, Diary.class);
            diary.initUser(userId);
        }
        diaryRepository.save(diary);
        Diary.DiaryResponse responseDto = modelMapper.map(diary, Diary.DiaryResponse.class);
        responseDto.setToday(isToday);

        return responseDto;
    }

    @Transactional(readOnly = true)
    public ArrayList<DailyResponse> getDailyDiaryOfMonth(Long userId, int retrieveYear, int retrieveMonth) {

        Set<String> holidays = utils.holidayArray(String.valueOf(retrieveYear));
        LocalDate startDate = LocalDate.of(retrieveYear, retrieveMonth, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
        List<DailyDiary> dailyDiaries = diaryRepository.findDiaryOfPeriodByUserIdAndStartDateAndEndDate(
            userId,
            startDate, endDate);
        ArrayList<DailyResponse> dailyResponses = new ArrayList<>();
        boolean isHoliday;
        for (DailyDiary daily : dailyDiaries) {
            LocalDate dailyDate = daily.getDiaryDate();
            if (dailyDate.getDayOfWeek() == DayOfWeek.SUNDAY || holidays.contains(
                dailyDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")))) {
                isHoliday = true;
            } else {
                isHoliday = false;
            }
            dailyResponses.add(DailyResponse.builder().diaryExists(daily.isDiaryExists())
                .diaryDate(daily.getDiaryDate()).isHoliday(isHoliday).build());
        }
        return dailyResponses;
    }
}
