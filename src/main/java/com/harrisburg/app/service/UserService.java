package com.harrisburg.app.service;

import com.harrisburg.app.domain.User;
import com.harrisburg.app.entity.ContactRelation;
import com.harrisburg.app.entity.UserInfo;

import java.util.List;

public interface UserService {

    UserInfo addUser(UserInfo userInfo);
    UserInfo validateUser(UserInfo userInfo);
    User findUserByUsername(String username);
    List<User> getAllContactByUserId(Integer userId);
    ContactRelation addContact(Integer userId, Integer currentUserId);
    Boolean isOldFriend(Integer userId, Integer contactId);
}
