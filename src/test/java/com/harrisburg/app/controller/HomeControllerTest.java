package com.harrisburg.app.controller;

import com.harrisburg.app.entity.UserInfo;
import com.harrisburg.app.exception.InvalidUserCredentialException;
import com.harrisburg.app.exception.UserExistedException;
import com.harrisburg.app.service.UserService;
import com.harrisburg.app.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(controllers = HomeController.class)
public class HomeControllerTest {

    @MockBean
    private UserServiceImpl userService;

    @Autowired
    private MockMvc mockMvc;

    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testHomeSuccess() throws Exception {
        this.mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().size(3));

        verifyNoInteractions(userService);
    }

    @Test
    public void testLoginInputInvalid() throws Exception {
        this.mockMvc.perform(post("/signin")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("username", "")
                .param("password", "")
        )
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().attributeHasFieldErrors("loginUser", "username", "password"))
                .andExpect(model().attribute("leftActive", true))
                .andExpect(model().size(3));

        verifyNoInteractions(userService);
    }

    @Test
    public void testLoginInvalidCredential() throws Exception {

        when(userService.validateUser(isA(UserInfo.class))).thenThrow(new InvalidUserCredentialException("Username or password incorrect"));

        this.mockMvc.perform(post("/signin")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("username", "abc")
                .param("password", "def")
        )
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().attribute("userValidationError", "Username or password incorrect"))
                .andExpect(model().attribute("leftActive", true))
                .andExpect(model().size(4));

        verify(userService, times(1)).validateUser(isA(UserInfo.class));
        verifyNoMoreInteractions(userService);
    }

    @Test
    public void testLoginSuccess() throws Exception {

        when(userService.validateUser(isA(UserInfo.class))).thenReturn(getMockedUserInfo());

        this.mockMvc.perform(post("/signin")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("username", "abc")
                .param("password", "def")
        )
                .andExpect(status().isOk())
                .andExpect(view().name("chat"))
                .andExpect(request().sessionAttribute("userId", 1))
                .andExpect(request().sessionAttribute("firstname", "Mock"))
                .andExpect(request().sessionAttribute("lastname", "Mock"));

        ArgumentCaptor<UserInfo> userInfoArgumentCaptor = ArgumentCaptor.forClass(UserInfo.class);
        verify(userService, times(1)).validateUser(userInfoArgumentCaptor.capture());
        verifyNoMoreInteractions(userService);

        UserInfo formObject = userInfoArgumentCaptor.getValue();

        Assertions.assertAll(
                () -> Assertions.assertEquals("abc", formObject.getUsername()),
                () -> Assertions.assertEquals("def", formObject.getPassword())
        );
    }

    @Test
    public void testSignUpInputInvalid() throws Exception {
        this.mockMvc.perform(post("/signup")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("username", "")
                .param("password", "")
        )
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().attributeHasFieldErrors("registerUser", "username", "password"))
                .andExpect(model().attribute("leftActive", false))
                .andExpect(model().size(3));

        verifyNoInteractions(userService);
    }

    @Test
    public void testSignUpUserExisted() throws Exception {

        when(userService.addUser(isA(UserInfo.class))).thenThrow(new UserExistedException("Username existed"));

        this.mockMvc.perform(post("/signup")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("username", "abc")
                .param("password", "def")
        )
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().attribute("userExistedError", "Username existed"))
                .andExpect(model().attribute("leftActive", false))
                .andExpect(model().size(4));

        ArgumentCaptor<UserInfo> userInfoArgumentCaptor = ArgumentCaptor.forClass(UserInfo.class);
        verify(userService, times(1)).addUser(userInfoArgumentCaptor.capture());
        verifyNoMoreInteractions(userService);
    }

    @Test
    public void testSignUpSuccess() throws Exception {

        when(userService.addUser(isA(UserInfo.class))).thenReturn(getMockedUserInfo());

        this.mockMvc.perform(post("/signup")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("username", "abc")
                .param("password", "def")
        )
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().attribute("registered", "Sign Up successfully. Please login"))
                .andExpect(model().attribute("leftActive", true))
                .andExpect(model().size(4));

        ArgumentCaptor<UserInfo> userInfoArgumentCaptor = ArgumentCaptor.forClass(UserInfo.class);
        verify(userService, times(1)).addUser(userInfoArgumentCaptor.capture());
        verifyNoMoreInteractions(userService);

        UserInfo formObject = userInfoArgumentCaptor.getValue();

        Assertions.assertAll(
                () -> Assertions.assertEquals("abc", formObject.getUsername()),
                () -> Assertions.assertEquals("def", formObject.getPassword())
        );
    }

    private UserInfo getMockedUserInfo() {
        return  UserInfo.builder()
                        .id(1)
                        .username("Mock")
                        .firstname("Mock")
                        .lastname("Mock")
                        .password("Mock")
                        .phone("123456789")
                        .build();
    }
}
