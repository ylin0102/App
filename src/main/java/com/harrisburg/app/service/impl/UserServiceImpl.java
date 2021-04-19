package com.harrisburg.app.service.impl;

import com.harrisburg.app.entity.UserInfo;
import com.harrisburg.app.exception.InvalidUserCredentialException;
import com.harrisburg.app.exception.UserExistedException;
import com.harrisburg.app.repository.UserInfoRepository;
import com.harrisburg.app.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserInfoRepository userInfoRepository;

    public UserServiceImpl(UserInfoRepository userInfoRepository) {
        this.userInfoRepository = userInfoRepository;
    }

    @Override
    public UserInfo addUser(UserInfo userInfo) {
        Optional<UserInfo> existedUser = userInfoRepository.findByUsername(userInfo.getUsername());

        if (existedUser.isPresent()) {
            throw new UserExistedException("User name is not available");
        }

        return userInfoRepository.save(userInfo);
    }

    @Override
    public UserInfo validateUser(UserInfo userInfo) {

        Optional<UserInfo> existedUser = userInfoRepository.findByUsername(userInfo.getUsername());

        if (!existedUser.isPresent() || !existedUser.get().getPassword().equals(userInfo.getPassword())) {
            throw new InvalidUserCredentialException("Username or password is not correct");
        }

        return existedUser.get();
    }
}
