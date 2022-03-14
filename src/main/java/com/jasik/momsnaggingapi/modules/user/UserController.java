package com.jasik.momsnaggingapi.modules.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor

public class UserController {

    private final UserService userService;

    @PostMapping("/user/save/")
    public ResponseEntity<User>saveUser(@RequestBody User user){
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/user/save").toUriString());
        return ResponseEntity.created(uri).body(userService.saveUser(user));
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<Optional<User>> getUser(
            @PathVariable("userId") Long userId
    ) {
        return ResponseEntity.ok().body(userService.getUser(userId));
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getUsers() {
        log.error("Error Test Msg kkk!");
        log.info("Info Test Msg zzz!!");
        return ResponseEntity.ok().body(userService.getUsers());
    }

}
