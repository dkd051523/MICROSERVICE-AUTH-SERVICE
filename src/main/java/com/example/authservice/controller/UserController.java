package com.example.authservice.controller;

import com.example.authservice.core.BaseResponse;
import com.example.authservice.exception.CustomException;
import com.example.authservice.model.request.PostLoginRequestBody;
import com.example.authservice.model.request.PostRegistryRequestBody;
import com.example.authservice.model.request.PostUserListRequestBody;
import com.example.authservice.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/login")
    private BaseResponse<?> postLogin(@RequestBody PostLoginRequestBody requestBody, HttpServletRequest servletRequest) throws Exception {
        return new BaseResponse<>(userService.postLogin(requestBody));
    }

    @PostMapping("/registry")
    private BaseResponse<?> postRegistry(@RequestBody PostRegistryRequestBody requestBody, HttpServletRequest servletRequest) throws CustomException {
        return new BaseResponse<>(userService.postRegistry(requestBody));
    }
    @PostMapping("/list")
    private BaseResponse<?> postList(@RequestBody PostUserListRequestBody requestBody, HttpServletRequest servletRequest){
        return new BaseResponse<>(userService.postUserList(requestBody));
    }
}
