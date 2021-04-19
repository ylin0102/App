package com.harrisburg.app.service;

import com.harrisburg.app.entity.UserInfo;

public interface UserService {

    UserInfo addUser(UserInfo userInfo);
    UserInfo validateUser(UserInfo userInfo);
}
