package com.moonraft.search.application.controller;

import com.moonraft.search.application.controller.request.RegistrationRequest;
import com.moonraft.search.application.mapper.InputOutputMapper;
import com.moonraft.search.domain.command.UserRegisterCommand;
import com.moonraft.search.domain.config.AppConstants;
import com.moonraft.search.domain.model.UserDetailsResponse;
import com.moonraft.search.domain.service.api.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService){this.userService=userService;}
    @GetMapping("userinfo")
    public UserDetailsResponse getUserInfo(@RequestParam Long id){
        return userService.getUser(id);
    }

    @PostMapping("register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody RegistrationRequest registrationRequest){
        UserRegisterCommand userRegisterCommand = InputOutputMapper.buildUserCommandFromUserRequest.apply(registrationRequest, AppConstants.USER);
        return new ResponseEntity<>(userService.registerUser(userRegisterCommand),HttpStatus.CREATED);
    }

}
