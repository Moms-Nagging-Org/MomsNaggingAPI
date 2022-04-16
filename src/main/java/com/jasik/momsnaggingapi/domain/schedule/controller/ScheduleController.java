package com.jasik.momsnaggingapi.domain.schedule.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/schedule")
public class ScheduleController {

//    특정 날짜의 할일/습관 리스트 조회
//    input :
//      20220416
//    output :
//        스케줄 리스트

//    추천 습관의 종류 리스트 조회
//    output :
//      추천 습관 종류 리스트

//    종류별 추천 습관 리스트 조회
//    output :
//      추천 습관 리스트

//    습관 직접 생성
//    input :
//      습관 이름
//      수행 시간
//      이행 주기
//      잔소리 알림 여부
//      잔소리 알림 시간
//    output:
//      스케줄

//    할일 생성
//    input:
//      할일 이름
//      수행 시간
//      잔소리 알림 여부
//      잔소리 알림 시간
//      비공개 설정
//    output:
//      스케줄

//    스케줄 수정
//    input:
//      할일/습관 id
//      변경 값
//    output:
//      스케줄

//    스케줄 조회
//    input:
//      스케줄 id
//    output:
//      스케줄

//    스케줄 삭제
//    input:
//      스케줄 id

//    스케줄 정렬 저장
//    input:
//      정렬

//    주간 평가 발급
//    input:
//    output:
//      평가 결과 or None

}
