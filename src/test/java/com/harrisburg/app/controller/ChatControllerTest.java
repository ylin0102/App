package com.harrisburg.app.controller;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.harrisburg.app.domain.*;
import com.harrisburg.app.exception.UserNotFoundException;
import com.harrisburg.app.service.impl.ChatServiceImpl;
import com.harrisburg.app.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.servlet.http.HttpSession;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest(controllers = ChatController.class)
public class ChatControllerTest {
    @MockBean
    private UserServiceImpl userService;

    @MockBean
    private ChatServiceImpl chatService;

    private MockHttpSession mockHttpSession;

    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
        mockHttpSession = new MockHttpSession();
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testSearchFriendSameUsernameError() throws Exception {
        mockHttpSession.setAttribute("username", "abc");

        MvcResult mvcResult = mockMvc.perform(get("/chat/search-friend")
                .param("username", "abc")
                .session(mockHttpSession)
        )
                .andExpect(status().isOk())
                .andReturn();

        SearchFriendResponse expectedResponse = SearchFriendResponse.builder()
                .success(false)
                .message("No User Found!")
                .build();

        verifyNoInteractions(userService);
        assertEquals(objectMapper.writeValueAsString(expectedResponse), mvcResult.getResponse().getContentAsString());
    }

    @Test
    public void testSearchFriendNoUserFoundError() throws Exception {
        mockHttpSession.setAttribute("username", "abc");
        when(userService.findUserByUsername(anyString())).thenThrow(new UserNotFoundException("No User Found"));

        MvcResult mvcResult = mockMvc.perform(get("/chat/search-friend")
                .param("username", "def")
                .session(mockHttpSession)
        )
                .andExpect(status().isOk())
                .andReturn();

        SearchFriendResponse expectedResponse = SearchFriendResponse.builder()
                .success(false)
                .message("No User Found")
                .build();

        verify(userService, times(1)).findUserByUsername(anyString());
        assertEquals(objectMapper.writeValueAsString(expectedResponse), mvcResult.getResponse().getContentAsString());
    }

    @Test
    public void testSearchFriendSuccess() throws Exception {
        mockHttpSession.setAttribute("username", "abc");
        mockHttpSession.setAttribute("userId", 1);
        when(userService.findUserByUsername(anyString())).thenReturn(getMockUser());
        when(userService.isOldFriend(anyInt(), anyInt())).thenReturn(true);

        MvcResult mvcResult = mockMvc.perform(get("/chat/search-friend")
                .param("username", "def")
                .session(mockHttpSession)
        )
                .andExpect(status().isOk())
                .andReturn();

        SearchFriendResponse expectedResponse = SearchFriendResponse.builder()
                .success(true)
                .user(getMockUser())
                .isNew(false)
                .build();

        verify(userService, times(1)).findUserByUsername(anyString());
        verify(userService, times(1)).isOldFriend(anyInt(), anyInt());
        assertEquals(objectMapper.writeValueAsString(expectedResponse), mvcResult.getResponse().getContentAsString());
    }

    @Test
    public void testSearchFriendSuccessAsNew() throws Exception {
        mockHttpSession.setAttribute("username", "abc");
        mockHttpSession.setAttribute("userId", 1);
        when(userService.findUserByUsername(anyString())).thenReturn(getMockUser());
        when(userService.isOldFriend(anyInt(), anyInt())).thenReturn(false);

        MvcResult mvcResult = mockMvc.perform(get("/chat/search-friend")
                .param("username", "def")
                .session(mockHttpSession)
        )
                .andExpect(status().isOk())
                .andReturn();

        SearchFriendResponse expectedResponse = SearchFriendResponse.builder()
                .success(true)
                .user(getMockUser())
                .isNew(true)
                .build();

        verify(userService, times(1)).findUserByUsername(anyString());
        verify(userService, times(1)).isOldFriend(anyInt(), anyInt());
        assertEquals(objectMapper.writeValueAsString(expectedResponse), mvcResult.getResponse().getContentAsString());
    }

    @Test
    public void testListContact() throws Exception {
        mockHttpSession.setAttribute("userId", 1);
        when(userService.getAllContactByUserId(anyInt())).thenReturn(getMockUserList());

        MvcResult mvcResult = mockMvc.perform(get("/chat/list-contacts")
                .session(mockHttpSession)
        )
                .andExpect(status().isOk())
                .andReturn();

        ListContactResponse expectedResponse = ListContactResponse.builder()
                .success(true)
                .contacts(getMockUserList())
                .build();

        verify(userService, times(1)).getAllContactByUserId(anyInt());
        assertEquals(objectMapper.writeValueAsString(expectedResponse), mvcResult.getResponse().getContentAsString());
    }

    @Test
    public void testAddFriendInvalidUserId() throws Exception {
        mockHttpSession.setAttribute("userId", 1);

        MvcResult mvcResult = mockMvc.perform(get("/chat/add-friend")
                .param("userId", "1")
                .session(mockHttpSession)
        )
                .andExpect(status().isOk())
                .andReturn();

        GetMessageFromThreadResponse expectedResponse = GetMessageFromThreadResponse.builder()
                .success(false)
                .message("Invalid User Id: Users can not add themselves as contacts")
                .build();

        verifyNoInteractions(userService);
        assertEquals(objectMapper.writeValueAsString(expectedResponse), mvcResult.getResponse().getContentAsString());
    }

    @Test
    public void testAddFriendSuccess() throws Exception {
        UUID uuid = Uuids.random();
        mockHttpSession.setAttribute("userId", 1);
        mockHttpSession.setAttribute("threadId", uuid);
        mockHttpSession.setAttribute("threadName", "test");
        mockHttpSession.setAttribute("updatedAt", "10:11");
        mockHttpSession.setAttribute("updatedDate", "2021/3/24");
        when(userService.addContact(anyInt(), anyInt())).thenReturn(null);
        when(chatService.startChat(anyInt(), anyInt())).thenReturn(getMockMessageList(uuid));

        MvcResult mvcResult = mockMvc.perform(get("/chat/add-friend")
                .param("userId", "2")
                .session(mockHttpSession)
        )
                .andExpect(status().isOk())
                .andReturn();

        GetMessageFromThreadResponse expectedResponse = GetMessageFromThreadResponse.builder()
                .success(true)
                .messages(getMockMessageList(uuid))
                .threadId(uuid)
                .threadName("test")
                .updatedAt("10:11")
                .updatedDate("2021/3/24")
                .build();

        verify(userService, times(1)).addContact(anyInt(), anyInt());
        verify(chatService, times(1)).startChat(anyInt(), anyInt());
        assertEquals(objectMapper.writeValueAsString(expectedResponse), mvcResult.getResponse().getContentAsString());
    }

    @Test
    public void testLoadUserThreadSuccess() throws Exception {
        mockHttpSession.setAttribute("userId", 1);
        when(chatService.getAllThreadsByUserId(anyInt())).thenReturn(getMockThreadList());

        GetAllUserThreadsResponse expectedResponse = GetAllUserThreadsResponse.builder()
                .success(true)
                .threads(getMockThreadList())
                .build();

        MvcResult mvcResult = mockMvc.perform(get("/chat/load-user-threads")
                .session(mockHttpSession)
        )
                .andExpect(status().isOk())
                .andReturn();

        verify(chatService, times(1)).getAllThreadsByUserId(anyInt());
        assertEquals(objectMapper.writeValueAsString(expectedResponse), mvcResult.getResponse().getContentAsString());
    }

    @Test
    public void testLoadThreadWithThreadIdWithPage() throws Exception {
        UUID uuid = Uuids.random();
        when(chatService.getMessageFromThread(any(UUID.class), anyInt())).thenReturn(getMockMessageList(uuid));

        GetMessageFromThreadResponse expectedResponse = GetMessageFromThreadResponse.builder()
                .success(true)
                .messages(getMockMessageList(uuid))
                .build();

        MvcResult mvcResult = mockMvc.perform(get("/chat/load-thread")
                .param("threadId", uuid.toString())
                .param("page", "3")
        )
                .andExpect(status().isOk())
                .andReturn();

        verify(chatService, times(1)).getMessageFromThread(any(UUID.class), anyInt());
        assertEquals(objectMapper.writeValueAsString(expectedResponse), mvcResult.getResponse().getContentAsString());
    }

    @Test
    public void testLoadThreadWithUserIdsSuccess() throws Exception {
        UUID uuid = Uuids.random();
        mockHttpSession.setAttribute("userId", 1);
        mockHttpSession.setAttribute("threadId", uuid);
        when(chatService.startChat(anyInt(), anyInt())).thenReturn(getMockMessageList(uuid));

        GetMessageFromThreadResponse expectedResponse = GetMessageFromThreadResponse.builder()
                .success(true)
                .messages(getMockMessageList(uuid))
                .threadId(uuid)
                .build();

        GetMessageFromThreadRequest request = GetMessageFromThreadRequest.builder()
                .userIds(Collections.singletonList(2))
                .build();

        MvcResult mvcResult = mockMvc.perform(post("/chat/load-thread")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request))
                .session(mockHttpSession)
        )
                .andExpect(status().isOk())
                .andReturn();

        verify(chatService, times(1)).startChat(anyInt(), anyInt());
        assertEquals(objectMapper.writeValueAsString(expectedResponse), mvcResult.getResponse().getContentAsString());
    }

    private List<ThreadDto> getMockThreadList() {
        return Arrays.asList(getMockThread(), getMockThread());
    }

    private ThreadDto getMockThread() {
        return ThreadDto.builder()
                .threadName("test")
                .updatedDate("2021/3/21")
                .updatedAt("10:24")
                .unreadCount(2)
                .lastMessage("test")
                .build();
    }

    private List<ThreadMessage> getMockMessageList(UUID uuid) {
        return Arrays.asList(getMockMessage(uuid), getMockMessage(uuid));
    }

    private ThreadMessage getMockMessage(UUID uuid) {
        return ThreadMessage.builder()
                .threadId(uuid)
                .userId(1)
                .content("test")
                .displayName("test")
                .build();
    }

    private List<User> getMockUserList() {
        return Arrays.asList(getMockUser(), getMockUser());
    }

    private User getMockUser() {
        return User.builder()
                .id(1)
                .username("test")
                .lastName("test")
                .firstName("test")
                .build();
    }
}
