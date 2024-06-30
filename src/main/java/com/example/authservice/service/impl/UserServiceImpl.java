package com.example.authservice.service.impl;
/*
 *  @author diemdz
 */

import com.example.authservice.configs.CustomUserDetails;
import com.example.authservice.configs.CustomUserDetailsService;
import com.example.authservice.entity.UserEntity;
import com.example.authservice.exception.CustomException;
import com.example.authservice.model.request.PostLoginRequestBody;
import com.example.authservice.model.request.PostRegistryRequestBody;
import com.example.authservice.model.request.PostUserListRequestBody;
import com.example.authservice.model.response.PostLoginResponseBody;
import com.example.authservice.model.response.PostRegistryResponseBody;
import com.example.authservice.model.response.PostUserListResponseBody;
import com.example.authservice.repository.UserRepository;
import com.example.authservice.service.UserService;
import com.example.authservice.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import jakarta.persistence.criteria.Predicate;


@Service
@Slf4j
@Transactional(rollbackFor = Throwable.class)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    @Autowired
    private CustomUserDetailsService userDetailsService;
    @Override
    public PostLoginResponseBody postLogin(PostLoginRequestBody requestBody) throws Exception {
        authenticate(requestBody.getUserName(), requestBody.getPassword());
         UserDetails userDetails = userDetailsService.loadUserByUsername(requestBody.getUserName());
         Optional<UserEntity> optional = userRepository.findByUserName(requestBody.getUserName());
         if(optional.isEmpty()){
             throw new CustomException(HttpStatus.BAD_REQUEST.value(), "Tài khoản không tồn tại");
         }
         String token = jwtUtil.generateToken(userDetails.getUsername());
        return PostLoginResponseBody.builder()
                .token(token)
                .refreshToken(UUID.randomUUID().toString())
                .userName(userDetails.getUsername())
                .build();
    }

    @Override
    public PostRegistryResponseBody postRegistry(PostRegistryRequestBody requestBody) throws CustomException {

        Optional<UserEntity> optionalUserEntity = userRepository.findByUserName(requestBody.getUserName());
        if (optionalUserEntity.isPresent()) {
            throw new CustomException(HttpStatus.BAD_REQUEST.value(), "Tài khoản đã tồn tại");
        }
        String password = RandomStringUtils.randomAlphanumeric(6);
        log.info("{} postRegistry requestBody {}", requestBody, password);
        UserEntity userEntity = UserEntity.builder()
                .id(UUID.randomUUID().toString())
                .userName(requestBody.getUserName())
                .password(passwordEncoder.encode(password))
                .build();
        userRepository.save(userEntity);

        return  PostRegistryResponseBody.builder()
                .userName(userEntity.getUserName())
                .passWord(password)
                .build();
    }

    @Override
    public List<PostUserListResponseBody> postUserList(PostUserListRequestBody requestBody) {
        Specification<UserEntity> employeeInfoEntitySpec = getUser(requestBody);
        List<UserEntity> userEntityList = userRepository.findAll(employeeInfoEntitySpec);
        return userEntityList.stream().map((e)->
              modelMapper.map(e, PostUserListResponseBody.class)
        ).toList();
    }
    private Specification<UserEntity> getUser(PostUserListRequestBody requestBody) {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.isNotEmpty(requestBody.getUserName())) {
                predicates.add(builder.like(root.get("userName"), "%" + requestBody.getUserName() + "%"));
            }

            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new CustomException(HttpStatus.UNAUTHORIZED.value(), "USER_DISABLED");
        } catch (BadCredentialsException e) {
            throw new CustomException(HttpStatus.UNAUTHORIZED.value(), "INVALID_CREDENTIALS");
        } catch (Exception e) {
            throw new CustomException(HttpStatus.UNAUTHORIZED.value(), "LOGIN_FAILED");
        }
    }
}
