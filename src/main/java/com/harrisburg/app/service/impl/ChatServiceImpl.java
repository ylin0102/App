package com.harrisburg.app.service.impl;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import com.harrisburg.app.domain.ThreadDto;
import com.harrisburg.app.domain.ThreadMessage;
import com.harrisburg.app.entity.UserInfo;
import com.harrisburg.app.entity.cassandra.ParticipantHashCode;
import com.harrisburg.app.entity.cassandra.Thread;
import com.harrisburg.app.entity.cassandra.UserThread;
import com.harrisburg.app.entity.cassandra.keys.UserThreadKey;
import com.harrisburg.app.repository.UserInfoRepository;
import com.harrisburg.app.repository.cassandra.MessageRepository;
import com.harrisburg.app.repository.cassandra.ParticipantHashCodeRepository;
import com.harrisburg.app.repository.cassandra.ThreadRepository;
import com.harrisburg.app.repository.cassandra.UserThreadRepository;
import com.harrisburg.app.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChatServiceImpl implements ChatService {

    private final MessageRepository messageRepository;
    private final ParticipantHashCodeRepository participantHashCodeRepository;
    private final ThreadRepository threadRepository;
    private final UserThreadRepository userThreadRepository;
    private final UserInfoRepository userInfoRepository;

    private HttpSession httpSession;

    public ChatServiceImpl(MessageRepository messageRepository,
                           ParticipantHashCodeRepository participantHashCodeRepository,
                           ThreadRepository threadRepository,
                           UserThreadRepository userThreadRepository, UserInfoRepository userInfoRepository) {
        this.messageRepository = messageRepository;
        this.participantHashCodeRepository = participantHashCodeRepository;
        this.threadRepository = threadRepository;
        this.userThreadRepository = userThreadRepository;
        this.userInfoRepository = userInfoRepository;
    }

    @Autowired
    public void setHttpSession(HttpSession httpSession) {
        this.httpSession = httpSession;
    }

    @Override
    public List<ThreadDto> getAllThreadsByUserId(Integer userId) {
        return userThreadRepository.findByKeyUserId(userId)
                .stream()
                .map(userThread -> ThreadDto.builder()
                        .threadId(userThread.getThreadId())
                        .threadName(userThread.getThreadName())
                        .lastMessage(userThread.getLastMessage())
                        .unreadCount(userThread.getUnreadCount())
                        .updatedAt(userThread.getKey().getUpdatedAt().getHour() + ":" + userThread.getKey().getUpdatedAt().getMinute())
                        .updatedDate(userThread.getKey().getUpdatedAt().toLocalDate().toString())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public List<ThreadMessage> getMessageFromThread(UUID threadId, Integer page) {
        return getMessage(threadId, page);
    }

    @Override
    public List<ThreadMessage> startChat(UUID threadId) {
        return getMessage(threadId, 1);
    }

    private List<ThreadMessage> getMessage(UUID threadId, Integer page) {
        return messageRepository.findByKeyThreadId(threadId)
                .stream()
                .map(message -> ThreadMessage.builder()
                        .displayName(message.getDisplayName())
                        .content(message.getContent())
                        .createdAt(message.getCreatedAt())
                        .userId(message.getUserId())
                        .threadId(message.getKey().getThreadId())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public List<ThreadMessage> startChat(Integer... userIds) {
        Integer participantsCode = getHashCodeForParticipants(userIds);

        Optional<ParticipantHashCode> participantHashCodeOptional = participantHashCodeRepository.findByParticipantHash(participantsCode);
        UUID threadId;
        if (!participantHashCodeOptional.isPresent()) {
            threadId = initializeChatForUsers(userIds);
        } else {
            threadId = participantHashCodeOptional.get().getThreadId();
        }
        httpSession.setAttribute("threadId", threadId);

        return getMessage(threadId, 1);
    }

    private UUID initializeChatForUsers(Integer[] userIds) {
        //1. Build Thread with thread ID
        Thread thread = threadRepository.save(Thread.builder()
                .threadId(Uuids.random())
                .createdAt(LocalDateTime.now())
                .participants(new HashSet<>(Arrays.asList(userIds)))
                .build());

        //2. Build bi-directional userThreads
        Map<Integer, String> threadNameMap = buildThreadNameMap(userIds);
        boolean isGroupChat = userIds.length != 2;
        Arrays.stream(userIds).forEach(userId -> userThreadRepository.save(UserThread.builder()
                .key(UserThreadKey.builder()
                        .userId(userId)
                        .updatedAt(thread.getCreatedAt())
                        .build())
                .threadId(thread.getThreadId())
                .threadName(buildThreadName(userId, threadNameMap, isGroupChat))
                .unreadCount(0)
                .lastMessage("")
                .build()));
        httpSession.setAttribute("updatedAt", thread.getCreatedAt().getHour() + ":" + thread.getCreatedAt().getMinute());
        httpSession.setAttribute("updatedDate", thread.getCreatedAt().toLocalDate().toString());
        //3. Build ParticipantHashCode
        Integer participantHashCode = getHashCodeForParticipants(userIds);
        participantHashCodeRepository.save(ParticipantHashCode.builder()
                .participantHash(participantHashCode)
                .threadId(thread.getThreadId())
                .build());

        return thread.getThreadId();
    }

    private String buildThreadName(Integer userId, Map<Integer, String> threadNameMap, boolean isGroupChat) {
        StringBuilder sb = new StringBuilder();
        if (isGroupChat) {
            sb.append("Group chat: ");
            threadNameMap.forEach((k, v) -> {
                if (!userId.equals(k)) {
                    sb.append(v.split(" ")[0]).append(", ");
                }
            });
            return sb.substring(0, sb.length() - 2);
        }

        threadNameMap.forEach((k, v) -> {
            if (!userId.equals(k)) {
                sb.append(v);
            }
        });

        return sb.toString();
    }

    private Map<Integer, String> buildThreadNameMap(Integer[] userIds) {
        Integer currentUserId = (Integer) httpSession.getAttribute("userId");
        Map<Integer, String> map = new HashMap<>();

        for (Integer userId : userIds) {
            if (currentUserId.equals(userId)) {
                String currentFirstName = (String) httpSession.getAttribute("firstname");
                String currentLastName = (String) httpSession.getAttribute("lastname");
                map.put(userId, currentFirstName + " " + currentLastName);
            } else {
                UserInfo userInfo = userInfoRepository.getOne(userId);
                map.put(userId, userInfo.getFirstname() + " " + userInfo.getLastname());
            }
        }

        httpSession.setAttribute("threadName", map.get(currentUserId));

        return map;
    }

    private Integer getHashCodeForParticipants(Integer[] userIds) {
        StringBuilder sb = new StringBuilder();
        Arrays.stream(userIds).forEach(userId -> sb.append(userId).append(","));

        return sb.toString().hashCode();
    }
}
