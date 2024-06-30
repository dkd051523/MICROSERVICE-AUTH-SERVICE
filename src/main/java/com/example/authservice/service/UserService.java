package com.example.authservice.service;
/*
 *  @author diemdz
 */

import com.example.authservice.exception.CustomException;
import com.example.authservice.model.request.PostLoginRequestBody;
import com.example.authservice.model.request.PostRegistryRequestBody;
import com.example.authservice.model.request.PostUserListRequestBody;
import com.example.authservice.model.response.PostLoginResponseBody;
import com.example.authservice.model.response.PostRegistryResponseBody;
import com.example.authservice.model.response.PostUserListResponseBody;

import java.util.List;

public interface UserService {
    PostLoginResponseBody postLogin(PostLoginRequestBody responseBody) throws Exception;
    PostRegistryResponseBody postRegistry(PostRegistryRequestBody responseBody) throws CustomException;

    List<PostUserListResponseBody> postUserList(PostUserListRequestBody responseBody);
}
