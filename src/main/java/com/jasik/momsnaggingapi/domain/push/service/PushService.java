package com.jasik.momsnaggingapi.domain.push.service;

import com.jasik.momsnaggingapi.domain.grade.Grade;
import com.jasik.momsnaggingapi.domain.nagging.Nagging;
import com.jasik.momsnaggingapi.domain.nagging.repository.NaggingRepository;
import com.jasik.momsnaggingapi.domain.push.Interface.PushNaggingInterface;
import com.jasik.momsnaggingapi.domain.push.Push;
import com.jasik.momsnaggingapi.domain.push.Push.PushListAdminRequest;
import com.jasik.momsnaggingapi.domain.push.Push.PushListAdminResponse;
import com.jasik.momsnaggingapi.domain.push.Push.PushType;
import com.jasik.momsnaggingapi.domain.push.repository.PushRepository;
import com.jasik.momsnaggingapi.domain.schedule.Schedule;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PushService {

    private final PushRepository pushRepository;
    private final NaggingRepository naggingRepository;
    private final ModelMapper modelMapper;

    @Transactional(readOnly = true)
    public Page<PushListAdminResponse> getPushes(PushType pushType, Pageable pageable) {
        Page<PushNaggingInterface> pushPage = pushRepository.findAllByPushTypeOrderByIdDesc(pushType, pageable);

        return pushPage.map(p -> PushListAdminResponse.builder()
                .id(p.getPush().getId())
                .pushDate(p.getPush().getPushDate())
                .pushTime(p.getPush().getPushTime())
                .pushType(p.getPush().getPushType())
                .mon(p.getPush().isMon())
                .tue(p.getPush().isTue())
                .wed(p.getPush().isWed())
                .thu(p.getPush().isThu())
                .fri(p.getPush().isFri())
                .sat(p.getPush().isSat())
                .sun(p.getPush().isSun())
                .title(p.getNagging().getTitle())
                .level1(p.getNagging().getLevel1())
                .level2(p.getNagging().getLevel2())
                .level3(p.getNagging().getLevel3())
                .build());
    }

    @Transactional
    public PushListAdminResponse postPush(PushListAdminRequest pushRequest) {
        Nagging nagging = naggingRepository.save(
            Nagging.builder().title(pushRequest.getTitle()).
                level1(pushRequest.getLevel1()).
                level2(pushRequest.getLevel2()).
                level3(pushRequest.getLevel3()).build());
        Push push = pushRepository.save(modelMapper.map(pushRequest, Push.class));
        push.initPushType();
        push.initNagging(nagging.getId());
        PushListAdminResponse pushDto = modelMapper.map(push, PushListAdminResponse.class);
        pushDto.setTitle(nagging.getTitle());
        pushDto.setLevel1(nagging.getLevel1());
        pushDto.setLevel2(nagging.getLevel2());
        pushDto.setLevel3(nagging.getLevel3());

        return pushDto;
    }
}
