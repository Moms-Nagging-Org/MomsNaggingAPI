package com.jasik.momsnaggingapi.domain.user.service;

import com.jasik.momsnaggingapi.domain.auth.service.Authservice;
import com.jasik.momsnaggingapi.domain.user.User;
import com.jasik.momsnaggingapi.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final Authservice authservice;
    private final ModelMapper modelMapper;

    public User.UserResponse findUser(String token) {
        User user = userRepository.findById(authservice.getId(token))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다."));
        return modelMapper.map(user, User.UserResponse.class);
    }

    public User.UserResponse editUser(String token) {
        User user = userRepository.findById(authservice.getId(token))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다."));
        return modelMapper.map(userRepository.findById(authservice.getId(token)), User.UserResponse.class);
    }

    public Optional<User> findUserById(Long id) {
        return userRepository.findById(id);
    }

    public Long findUserIdByPersonalId(String personalId) {
        Optional<User> user = userRepository.findByPersonalId(personalId);
        return user.map(User::getId).orElse(null);
    }
}
