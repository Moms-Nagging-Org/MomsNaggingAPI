package com.jasik.momsnaggingapi.domain.follow.service;


import com.jasik.momsnaggingapi.domain.follow.Follow;
import com.jasik.momsnaggingapi.domain.follow.Follow.FollowResponse;
import com.jasik.momsnaggingapi.domain.follow.repository.FollowRepository;
import com.jasik.momsnaggingapi.domain.user.User;
import com.jasik.momsnaggingapi.domain.user.repository.UserRepository;
import com.jasik.momsnaggingapi.infra.common.ErrorCode;
import com.jasik.momsnaggingapi.infra.common.exception.NotFoundException;
import com.jasik.momsnaggingapi.infra.common.exception.NotValidException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<FollowResponse> getFollowers(Long userId) {
        //TODO: 유저 조회 시 차단 관계인 유저는 목록에서 제외
        return followRepository.findFollowersByUserId(userId, false);
    }

    @Transactional(readOnly = true)
    public List<FollowResponse> getFollowings(Long userId) {

        return followRepository.findFollowingsByUserId(userId);
    }

    @Transactional
    public FollowResponse postFollowing(User user, Long toUserId) {
        Optional<Follow> optional = followRepository.findByFromUserAndToUser(user.getId(), toUserId);
        if (optional.isPresent()) {
            throw new NotValidException("계정을 팔로우할 수 없습니다.",
                ErrorCode.FOLLOW_NOT_VALID);
        } else {
            Follow newFollow = Follow.builder().fromUser(user.getId()).toUser(toUserId).build();
            followRepository.save(newFollow);
            User toUser = userRepository.findById(toUserId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));
            return FollowResponse.builder().nickName(toUser.getNickName()).userId(toUserId).personalId(toUser.getPersonalId()).statusMsg(toUser.getStatusMsg()).build();
        }
    }

    @Transactional
    public void deleteFollowing(User user, Long toUserId) {
        Optional<Follow> optional = followRepository.findByFromUserAndToUser(user.getId(), toUserId);
        if (optional.isPresent() && !optional.get().checkBlocked()) {
            followRepository.delete(optional.get());
        } else {
            throw new NotValidException("계정 팔로우 정보가 없습니다.",
                ErrorCode.FOLLOW_NOT_FOUND);
        }
    }
    @Transactional(readOnly = true)
    public List<FollowResponse> getBlock(Long userId) {
        return followRepository.findFollowersByUserId(userId, true);
    }
    @Transactional
    public FollowResponse postBlock(User user, Long toUserId) {
        int updatedCnt = followRepository.updateFollowWithBlock(toUserId, user.getId());
        if (updatedCnt == 0) {
            Follow newFollow = Follow.threeBuilder().fromUser(toUserId).toUser(user.getId()).isBlocked(true).build();
            followRepository.save(newFollow);
        }
        User toUser = userRepository.findById(toUserId)
            .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));
        return FollowResponse.builder().nickName(toUser.getNickName()).userId(toUserId).personalId(toUser.getPersonalId()).statusMsg(toUser.getStatusMsg()).build();
    }

    @Transactional
    public void deleteBlock(User user, Long toUserId) {
        followRepository.deleteByFromUserAndToUser(toUserId, user.getId());
    }
}
