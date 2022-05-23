package com.jasik.momsnaggingapi.domain.user.service;

import com.jasik.momsnaggingapi.domain.auth.exception.LoginFailureException;
import com.jasik.momsnaggingapi.domain.auth.jwt.AuthToken;
import com.jasik.momsnaggingapi.domain.auth.jwt.AuthTokenProvider;
import com.jasik.momsnaggingapi.domain.auth.service.Authservice;
import com.jasik.momsnaggingapi.domain.user.User;
import com.jasik.momsnaggingapi.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final Authservice authservice;
    private final ModelMapper modelMapper;
    private final AuthTokenProvider authTokenProvider;

    /**
     *  유저의 존재 유무를 파악하여 로그인 / 회원가입
     * @param providerCode
     * @return
     */
    public Optional<User> existUser(String providerCode) {
        return userRepository.findByProviderCode(providerCode);
    }

    /**
     *  유저의 id가 중복된 값을 가지는지 확인
     *  @param personalId
     */
    public Boolean validateDuplicatedId(String personalId) {
        return userRepository.findByPersonalId(personalId).isPresent();
    }

    @Transactional
    public User.AuthResponse registerUser(User.CreateRequest request) {
        User user = userRepository.save(
                User.builder()
                        .email(request.getEmail())
                        .provider(request.getProvider())
                        .providerCode(request.getCode())
                        .device(request.getDevice())
                        .personalId(request.getPersonalId())
                        .nickName(request.getNickname())
                        .build());
        AuthToken authToken = authTokenProvider.createToken(request.getProvider(), user.getEmail(), user.getPersonalId());
        return new User.AuthResponse(authToken.getToken());
    }

    public User.AuthResponse loginUser(User.AuthRequest request) {
        // TODO: provider 도 확인
        User user = userRepository.findByProviderCode(request.getCode()).orElseThrow(LoginFailureException::new);;

        AuthToken authToken = authTokenProvider.createToken(request.getProvider(), user.getEmail(), user.getPersonalId());
        return new User.AuthResponse(authToken.getToken());
    }

    public User.UserResponse findUser(String token) {
        return modelMapper.map(userRepository.findByPersonalId(authservice.getPersonalId(token)), User.UserResponse.class);
    }

    public Optional<User> findUserByPersonalId(String id) {
        return userRepository.findByPersonalId(id);
    }

    public Long findUserIdByPersonalId(String personalId) {
        Optional<User> user = userRepository.findByPersonalId(personalId);
        return user.map(User::getId).orElse(null);
    }
}
