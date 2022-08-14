package com.jasik.momsnaggingapi.domain.push.service;

import com.jasik.momsnaggingapi.domain.grade.Grade;
import com.jasik.momsnaggingapi.domain.nagging.Nagging;
import com.jasik.momsnaggingapi.domain.nagging.repository.NaggingRepository;
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
    public List<PushListAdminResponse> getPushes(PushType pushType) {
        ArrayList<PushListAdminResponse> pushResponseList = new ArrayList<>();
        List<Push> pushes = pushRepository.findAllByPushTypeOrderByIdDesc(pushType);
        for (Push push : pushes) {
            Nagging nagging = naggingRepository.getNaggingById(push.getNaggingId());
            PushListAdminResponse pushResponse = modelMapper.map(push, PushListAdminResponse.class);
            pushResponse.setTitle(nagging.getTitle());
            pushResponse.setLevel1(nagging.getLevel1());
            pushResponse.setLevel2(nagging.getLevel2());
            pushResponse.setLevel3(nagging.getLevel3());
            pushResponseList.add(pushResponse);
        }
        return pushResponseList;
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
