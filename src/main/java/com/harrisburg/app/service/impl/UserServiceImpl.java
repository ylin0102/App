package com.harrisburg.app.service.impl;

import com.harrisburg.app.domain.User;
import com.harrisburg.app.entity.ContactRelation;
import com.harrisburg.app.entity.UserInfo;
import com.harrisburg.app.exception.InvalidUserCredentialException;
import com.harrisburg.app.exception.UserExistedException;
import com.harrisburg.app.exception.UserNotFoundException;
import com.harrisburg.app.repository.ContactRelationRepository;
import com.harrisburg.app.repository.UserInfoRepository;
import com.harrisburg.app.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserInfoRepository userInfoRepository;
    private final ContactRelationRepository contactRelationRepository;

    public UserServiceImpl(UserInfoRepository userInfoRepository, ContactRelationRepository contactRelationRepository) {
        this.userInfoRepository = userInfoRepository;
        this.contactRelationRepository = contactRelationRepository;
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

    @Override
    public User findUserByUsername(String username) {
        return userInfoRepository.findByUsername(username)
                .map(userInfo -> User.builder()
                        .id(userInfo.getId())
                        .username(username)
                        .firstName(userInfo.getFirstname())
                        .lastName(userInfo.getLastname())
                        .build())
                .orElseThrow(() -> new UserNotFoundException("No User Find"));
    }

    @Override
    public List<User> getAllContactByUserId(Integer userId) {
        List<ContactRelation> contactRelationList = contactRelationRepository.findByUserId(userId);

        return contactRelationList.stream()
                .map(contactRelation -> userInfoRepository.getOne(contactRelation.getContactId()))
                .map(userInfo -> User.builder()
                        .id(userInfo.getId())
                        .username(userInfo.getUsername())
                        .firstName(userInfo.getFirstname())
                        .lastName(userInfo.getLastname())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public ContactRelation addContact(Integer userId, Integer currentUserId) {
        ContactRelation contactRelation = ContactRelation.builder()
                .userId(currentUserId)
                .contactId(userId)
                .build();

        //Two directional user-contact relationship
        contactRelationRepository.save(ContactRelation.builder()
                .userId(userId)
                .contactId(currentUserId)
                .build()
        );

        return contactRelationRepository.save(contactRelation);
    }

    @Override
    public Boolean isOldFriend(Integer userId, Integer contactId) {
        return contactRelationRepository.findByUserIdAndContactId(userId, contactId).isPresent();
    }


}
