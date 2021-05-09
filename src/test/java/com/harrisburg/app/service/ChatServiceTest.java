package com.harrisburg.app.service;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import com.harrisburg.app.domain.ThreadDto;
import com.harrisburg.app.domain.ThreadMessage;
import com.harrisburg.app.entity.UserInfo;
import com.harrisburg.app.entity.cassandra.Message;
import com.harrisburg.app.entity.cassandra.ParticipantHashCode;
import com.harrisburg.app.entity.cassandra.Thread;
import com.harrisburg.app.entity.cassandra.UserThread;
import com.harrisburg.app.entity.cassandra.keys.MessageKey;
import com.harrisburg.app.entity.cassandra.keys.UserThreadKey;
import com.harrisburg.app.repository.UserInfoRepository;
import com.harrisburg.app.repository.cassandra.MessageRepository;
import com.harrisburg.app.repository.cassandra.ParticipantHashCodeRepository;
import com.harrisburg.app.repository.cassandra.ThreadRepository;
import com.harrisburg.app.repository.cassandra.UserThreadRepository;
import com.harrisburg.app.service.impl.ChatServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class ChatServiceTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private ParticipantHashCodeRepository participantHashCodeRepository;

    @Mock
    private ThreadRepository threadRepository;

    @Mock
    private UserThreadRepository userThreadRepository;

    @Mock
    private UserInfoRepository userInfoRepository;

    private HttpSession httpSession;

    @InjectMocks
    private ChatServiceImpl chatService;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
        httpSession = mock(HttpSession.class);
        chatService.setHttpSession(httpSession);
    }

    @Test
    public void testGetAllThreadsByUserIdSuccess() {
        when(userThreadRepository.findByKeyUserId(anyInt())).thenReturn(getMockUserThreadList());

        List<ThreadDto> threadDtos = chatService.getAllThreadsByUserId(1);

        verify(userThreadRepository, times(1)).findByKeyUserId(anyInt());
        assertEquals("14:12", threadDtos.get(0).getUpdatedAt());
        assertEquals("11:55", threadDtos.get(1).getUpdatedAt());
    }

    @Test
    public void testGetMessageFromThreadSuccess() {
        when(messageRepository.findByKeyThreadId(any(UUID.class))).thenReturn(getMockMessageList());

        List<ThreadMessage> messages = chatService.getMessageFromThread(Uuids.random(), 1);

        verify(messageRepository, times(1)).findByKeyThreadId(any(UUID.class));

        assertEquals(2, messages.size());
        assertEquals("test", messages.get(0).getDisplayName());
    }

    @Test
    public void testStartChatWithThreadIdSuccess() {
        when(messageRepository.findByKeyThreadId(any(UUID.class))).thenReturn(getMockMessageList());

        List<ThreadMessage> messages = chatService.startChat(Uuids.random());

        verify(messageRepository, times(1)).findByKeyThreadId(any(UUID.class));

        assertEquals(2, messages.size());
        assertEquals("test", messages.get(0).getDisplayName());
    }

    @Test
    public void testStartChatWithChatExisted() {
        when(participantHashCodeRepository.findByParticipantHash(anyInt())).thenReturn(Optional.of(getMockParticipantCode()));
        doNothing().when(httpSession).setAttribute(anyString(), any());
        when(messageRepository.findByKeyThreadId(any(UUID.class))).thenReturn(getMockMessageList());

        List<ThreadMessage> messages = chatService.startChat(1, 2);

        verify(participantHashCodeRepository, times(1)).findByParticipantHash(anyInt());
        verify(httpSession, times(1)).setAttribute(anyString(), any());
        verify(messageRepository, times(1)).findByKeyThreadId(any(UUID.class));

        assertEquals(2, messages.size());
        assertEquals("test", messages.get(0).getDisplayName());
    }

    @Test
    public void testStartChatWithOneToOneSuccess() {
        when(participantHashCodeRepository.findByParticipantHash(anyInt())).thenReturn(Optional.empty());
        doNothing().when(httpSession).setAttribute(anyString(), any());
        when(messageRepository.findByKeyThreadId(any(UUID.class))).thenReturn(getMockMessageList());
        when(threadRepository.save(any(Thread.class))).thenReturn(getMockThread());
        when(httpSession.getAttribute("userId")).thenReturn(1);
        when(userThreadRepository.save(any(UserThread.class))).thenReturn(getMockUserThread(LocalDateTime.of(2021, 12, 3, 10, 10)));
        when(participantHashCodeRepository.save(any(ParticipantHashCode.class))).thenReturn(null);
        when(userInfoRepository.getOne(1)).thenReturn(getMockedUserInfo(1).get());
        when(userInfoRepository.getOne(2)).thenReturn(getMockedUserInfo(2).get());


        List<ThreadMessage> messages = chatService.startChat(1, 2);

        verify(participantHashCodeRepository, times(1)).findByParticipantHash(anyInt());
        verify(httpSession, times(4)).setAttribute(anyString(), any());
        verify(httpSession, times(3)).getAttribute(anyString());
        verify(messageRepository, times(1)).findByKeyThreadId(any(UUID.class));
        verify(threadRepository, times(1)).save(any(Thread.class));
        verify(userThreadRepository, times(2)).save(any(UserThread.class));
        verify(participantHashCodeRepository, times(1)).save(any(ParticipantHashCode.class));

        assertEquals(2, messages.size());
        assertEquals("test", messages.get(0).getDisplayName());
    }

    private Optional<UserInfo> getMockedUserInfo(Integer id) {
        return Optional.of(
                UserInfo.builder()
                        .id(id)
                        .username("Mock")
                        .firstname("Mock")
                        .lastname("Mock")
                        .password("Mock")
                        .phone("123456789")
                        .build()
        );
    }

    private Thread getMockThread() {
        return Thread.builder()
                .threadId(Uuids.random())
                .createdAt(LocalDateTime.of(2021, 12, 3, 10, 10))
                .build();
    }

    private ParticipantHashCode getMockParticipantCode() {
        return ParticipantHashCode.builder()
                .threadId(Uuids.random())
                .participantHash(1)
                .build();
    }

    private List<Message> getMockMessageList() {
        return Arrays.asList(getMockMessage(), getMockMessage());
    }

    private Message getMockMessage() {
        return Message.builder()
                .displayName("test")
                .content("test")
                .createdAt(LocalDateTime.now())
                .userId(1)
                .key(MessageKey.builder()
                        .threadId(Uuids.random())
                        .messageId(Uuids.timeBased())
                        .build())
                .build();
    }

    private List<UserThread> getMockUserThreadList() {
        return Arrays.asList(getMockUserThread(LocalDateTime.of(2000, 2, 12, 14, 12)),
                getMockUserThread(LocalDateTime.of(2001, 2, 12, 11, 55)));
    }

    private UserThread getMockUserThread(LocalDateTime dateTime) {
        return UserThread.builder()
                .key(UserThreadKey.builder()
                        .userId(1)
                        .updatedAt(dateTime)
                        .build())
                .threadName("Test")
                .lastMessage("Test")
                .unreadCount(1)
                .build();
    }
}
