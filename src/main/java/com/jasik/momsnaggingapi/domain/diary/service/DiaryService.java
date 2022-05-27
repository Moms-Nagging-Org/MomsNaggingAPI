package com.jasik.momsnaggingapi.domain.diary.service;

import com.jasik.momsnaggingapi.domain.diary.Diary;
import com.jasik.momsnaggingapi.domain.diary.Diary.DiaryResponse;
import com.jasik.momsnaggingapi.domain.diary.Diary.DailyResponse;
import com.jasik.momsnaggingapi.domain.diary.repository.DiaryRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
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

    @Transactional(readOnly = true)
    public Diary.DiaryResponse getDiary(LocalDate retrieveDate) {

        Long userId = 1L;
        boolean isToday = LocalDate.now().isEqual(retrieveDate);

        Optional<Diary> diary = diaryRepository.findByUserIdAndDiaryDate(userId, retrieveDate);
        if (diary.isPresent()){
            Diary.DiaryResponse responseDto = modelMapper.map(diary.get(), Diary.DiaryResponse.class);
            responseDto.setToday(isToday);
            return responseDto;
        }
        else {
            return new DiaryResponse("", "", retrieveDate, isToday);
        }
    }

    @Transactional()
    public Diary.DiaryResponse putDiary(Diary.DiaryRequest requestDto) {

        Long userId = 1L;
        boolean isToday = LocalDate.now().isEqual(requestDto.getDiaryDate());

        Optional<Diary> optionalDiary = diaryRepository.findByUserIdAndDiaryDate(userId, requestDto.getDiaryDate());
        Diary diary;
        if (optionalDiary.isPresent()){
            diary = optionalDiary.get();
            diary.updateDiary(requestDto.getTitle(), requestDto.getContext());
        }
        else {
            diary = modelMapper.map(requestDto, Diary.class);
            diary.initUser(userId);
        }
        diaryRepository.save(diary);
        Diary.DiaryResponse responseDto = modelMapper.map(diary, Diary.DiaryResponse.class);
        responseDto.setToday(isToday);

        return responseDto;
    }
    @Transactional(readOnly = true)
    public List<DailyResponse> getDailyDiaryOfMonth(int retrieveYear, int retrieveMonth) {

        Long userId = 1L;
        LocalDate startDate = LocalDate.of(retrieveYear, retrieveMonth, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
        return diaryRepository.findDiaryOfPeriodByUserIdAndStartDateAndEndDate(userId,
            startDate, endDate);
    }
}
