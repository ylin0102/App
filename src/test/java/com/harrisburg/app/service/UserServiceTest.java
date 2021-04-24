package com.harrisburg.app.service;

import com.harrisburg.app.entity.UserInfo;
import com.harrisburg.app.exception.InvalidUserCredentialException;
import com.harrisburg.app.exception.UserExistedException;
import com.harrisburg.app.repository.UserInfoRepository;
import com.harrisburg.app.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Mock
    private UserInfoRepository userInfoRepository;

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
