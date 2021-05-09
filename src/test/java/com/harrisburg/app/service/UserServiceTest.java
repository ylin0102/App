package com.harrisburg.app.service;

import com.harrisburg.app.domain.User;
import com.harrisburg.app.entity.ContactRelation;
import com.harrisburg.app.entity.UserInfo;
import com.harrisburg.app.exception.InvalidUserCredentialException;
import com.harrisburg.app.exception.UserExistedException;
import com.harrisburg.app.exception.UserNotFoundException;
import com.harrisburg.app.repository.ContactRelationRepository;
import com.harrisburg.app.repository.UserInfoRepository;
import com.harrisburg.app.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Mock
    private UserInfoRepository userInfoRepository;

    @Mock
    private ContactRelationRepository contactRelationRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAddUserSuccess() {
        UserInfo mockedInput = UserInfo.builder().username("abc").build();

        when(userInfoRepository.findByUsername(anyString())).thenReturn(getEmptyMockedUserInfo());
        when(userInfoRepository.save(mockedInput)).thenReturn(mockedInput);

        UserInfo returnedUserInfo = userService.addUser(mockedInput);

        verify(userInfoRepository, times(1)).findByUsername(anyString());
        verify(userInfoRepository, times(1)).save(mockedInput);

        Assertions.assertEquals("abc", returnedUserInfo.getUsername());
    }

    @Test
    public void testAddUserError() {
        UserInfo mockedInput = UserInfo.builder().username("abc").build();

        when(userInfoRepository.findByUsername(anyString())).thenReturn(getMockedUserInfo());
        when(userInfoRepository.save(mockedInput)).thenReturn(mockedInput);

        UserExistedException exception = Assertions.assertThrows(UserExistedException.class, () -> userService.addUser(mockedInput));

        verify(userInfoRepository, times(1)).findByUsername(anyString());
        verify(userInfoRepository, times(0)).save(mockedInput);

        Assertions.assertEquals("User name is not available", exception.getMessage());
    }

    @Test
    public void testValidateUserSuccess() {
        UserInfo mockedInput = UserInfo.builder().username("Mock").password("Mock").build();

        when(userInfoRepository.findByUsername(anyString())).thenReturn(getMockedUserInfo());

        UserInfo returnedUserInfo = userService.validateUser(mockedInput);

        verify(userInfoRepository, times(1)).findByUsername(anyString());
        Assertions.assertEquals("Mock", returnedUserInfo.getUsername());
    }

    @Test
    public void testValidateUserUsernameNotFound() {
        UserInfo mockedInput = UserInfo.builder().username("Mock").password("Mock").build();

        when(userInfoRepository.findByUsername(anyString())).thenReturn(getEmptyMockedUserInfo());

        InvalidUserCredentialException exception = Assertions.assertThrows(InvalidUserCredentialException.class, () -> userService.validateUser(mockedInput));

        verify(userInfoRepository, times(1)).findByUsername(anyString());

        Assertions.assertEquals("Username or password is not correct", exception.getMessage());
    }

    @Test
    public void testValidateUserPasswordIncorrect() {
        UserInfo mockedInput = UserInfo.builder().username("Mock").password("incorrect").build();

        when(userInfoRepository.findByUsername(anyString())).thenReturn(getMockedUserInfo());

        InvalidUserCredentialException exception = Assertions.assertThrows(InvalidUserCredentialException.class, () -> userService.validateUser(mockedInput));

        verify(userInfoRepository, times(1)).findByUsername(anyString());

        Assertions.assertEquals("Username or password is not correct", exception.getMessage());
    }

    @Test
    public void testFindUserByUserNameSuccess() {
        when(userInfoRepository.findByUsername(anyString())).thenReturn(getMockedUserInfo());

        User user = userService.findUserByUsername("abc");

        Assertions.assertAll(
                () -> Assertions.assertEquals("abc", user.getUsername()),
                () -> Assertions.assertEquals("Mock", user.getFirstName()),
                () -> Assertions.assertEquals("Mock", user.getLastName())
        );

        verify(userInfoRepository, times(1)).findByUsername(anyString());
    }

    @Test
    public void testFindUserByUserNameException() {
        when(userInfoRepository.findByUsername(anyString())).thenReturn(getEmptyMockedUserInfo());

        UserNotFoundException exception = Assertions.assertThrows(UserNotFoundException.class, () -> userService.findUserByUsername("abc"));

        Assertions.assertEquals("No User Find", exception.getMessage());
        verify(userInfoRepository, times(1)).findByUsername(anyString());
    }

    @Test
    public void testGetAllContactByUserIdSuccess() {
        when(contactRelationRepository.findByUserId(anyInt())).thenReturn(getMockContactRelationList());
        when(userInfoRepository.getOne(anyInt())).thenReturn(getMockedUserInfo().get());

        List<User> users = userService.getAllContactByUserId(1);

        verify(contactRelationRepository, times(1)).findByUserId(anyInt());
        verify(userInfoRepository, times(2)).getOne(anyInt());

        users.forEach(user -> Assertions.assertEquals("Mock", user.getUsername()));
    }

    @Test
    public void testAddContactSuccess() {
        when(contactRelationRepository.save(any(ContactRelation.class))).thenReturn(getMockContactRelation());

        ContactRelation contactRelation = userService.addContact(1, 2);

        verify(contactRelationRepository, times(2)).save(any(ContactRelation.class));
        Assertions.assertEquals(2, contactRelation.getUserId());
        Assertions.assertEquals(3, contactRelation.getContactId());
    }

    @Test
    public void testIsOldFriendTrue() {
        when(contactRelationRepository.findByUserIdAndContactId(1, 2)).thenReturn(Optional.of(getMockContactRelation()));

        Boolean isOldFriend = userService.isOldFriend(1,2);

        verify(contactRelationRepository, times(1)).findByUserIdAndContactId(1, 2);
        Assertions.assertEquals(true, isOldFriend);
    }

    private List<ContactRelation> getMockContactRelationList() {
        List<ContactRelation> list = new ArrayList<>();
        list.add(ContactRelation.builder()
                .id(1)
                .contactId(2)
                .userId(3)
                .build());
        list.add(ContactRelation.builder()
                .id(2)
                .contactId(3)
                .userId(2)
                .build());
        return list;
    }

    private ContactRelation getMockContactRelation() {
        return ContactRelation.builder()
                .id(2)
                .contactId(3)
                .userId(2)
                .build();
    }

    private Optional<UserInfo> getMockedUserInfo() {
        return Optional.of(
                UserInfo.builder()
                .username("Mock")
                .firstname("Mock")
                .lastname("Mock")
                .password("Mock")
                .phone("123456789")
                .build()
        );
    }

    private Optional<UserInfo> getEmptyMockedUserInfo() {
        return Optional.empty();
    }
}
