package com.jasik.momsnaggingapi.domain.user.service;

import com.jasik.momsnaggingapi.domain.user.User;
import com.jasik.momsnaggingapi.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public User.UserResponse findUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다."));
        return modelMapper.map(user, User.UserResponse.class);
    }

    public List<User.AdminResponse> findAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().map(p -> modelMapper.map(p, User.AdminResponse.class)).collect(Collectors.toList());
    }

    @Transactional
    public User.Response editUser(Long id, User.UpdateRequest user) {
        User existUser = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다."));

        if(StringUtils.isNotBlank(user.getNickName())) {
            existUser.setNickName(user.getNickName());
        }
        if(user.getStatusMsg() != null) {
            if(StringUtils.isBlank(user.getStatusMsg())) {
                existUser.setStatusMsg("오늘 하루도 파이팅 🔥");
            } else {
                existUser.setStatusMsg(user.getStatusMsg());
            }
        }
        if(user.getNaggingLevel() != null) {
            existUser.setNaggingLevel(user.getNaggingLevel());
        }
        if (user.getAllowRoutineNotice() != null) {
            existUser.setAllowRoutineNotice(user.getAllowRoutineNotice());
        }
        if (user.getAllowTodoNotice() != null) {
            existUser.setAllowTodoNotice(user.getAllowTodoNotice());
        }
        if (user.getAllowWeeklyNotice() != null) {
            existUser.setAllowWeeklyNotice(user.getAllowWeeklyNotice());
        }
        if (user.getAllowOtherNotice() != null) {
            existUser.setAllowOtherNotice(user.getAllowOtherNotice());
        }

        return modelMapper.map(userRepository.save(existUser), User.Response.class);
    }

    @Transactional
    public User.Response removeUser(Long id) {
        userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다."));

        userRepository.deleteById(id);

        User.Response res = new User.Response();
        res.setId(id);

        return res;
    }

    public Optional<User> findUserById(Long id) {
        return userRepository.findById(id);
    }

    public Long findUserIdByPersonalId(String personalId) {
        Optional<User> user = userRepository.findByPersonalId(personalId);
        return user.map(User::getId).orElse(null);
    }
}
