package com.jasik.momsnaggingapi.domain.auth.service;

import com.jasik.momsnaggingapi.domain.auth.exception.LoginFailureException;
import com.jasik.momsnaggingapi.domain.auth.jwt.AuthToken;
import com.jasik.momsnaggingapi.domain.auth.jwt.AuthTokenProvider;
import com.jasik.momsnaggingapi.domain.user.User;
import com.jasik.momsnaggingapi.domain.user.repository.UserRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final AuthTokenProvider authTokenProvider;

    public Long getId(String token) {
        Claims claims = authTokenProvider.getTokenClaim(token);
        if (claims == null) {
            return null;
        }

        return Long.parseLong(claims.getSubject());
    }

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
                        .firebaseToken(request.getFirebaseToken())
                        .build());
        AuthToken authToken = authTokenProvider.createToken(user.getId(), request.getProvider(), user.getEmail(), user.getPersonalId());
        return new User.AuthResponse(authToken.getToken());
    }

    public User.AuthResponse loginUser(User.AuthRequest request) {
        // TODO: provider 도 확인
        User user = userRepository.findByProviderCodeAndProvider(request.getCode(), request.getProvider()).orElseThrow(LoginFailureException::new);;

        user.setFirebaseToken(request.getFirebaseToken());
        user.setDevice(request.getDevice());

        userRepository.save(user);

        AuthToken authToken = authTokenProvider.createToken(user.getId(), request.getProvider(), user.getEmail(), user.getPersonalId());
        return new User.AuthResponse(authToken.getToken());
    }

    public User loadUserById(String id) {
        return userRepository.findById(Long.valueOf(id)).orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 유저 아이디입니다."));
    }

//    @Override
//    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
//        User user = userRepository.findById(Long.valueOf(id)).orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 유저 아이디입니다."));
//
//        return new Auth();
//    }
}
