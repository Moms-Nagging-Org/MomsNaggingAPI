package com.jasik.momsnaggingapi.modules.user;

import com.jasik.momsnaggingapi.modules.user.dtos.UserSaveDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "UserController!!!")
public class UserController {

    private final UserService userService;

    @Operation(description="유저 이름과 이메일 값으로 새로운 유저 생성.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "API 정상 작동",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = UserSaveDto.class))
                    }),
            @ApiResponse(responseCode = "404", description = "찾지 못함", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @PostMapping("/user/save/")
    public ResponseEntity<User>saveUser(@RequestBody UserSaveDto userDto){
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/user/save").toUriString());
        return ResponseEntity.created(uri).body(userService.saveUser(userDto.toEntity()));
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<Optional<User>> getUser(
            @Schema(description = "유저 ID", required = true, example = "12")
            @PathVariable Long userId
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
