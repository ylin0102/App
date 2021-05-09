package com.harrisburg.app.service;

import com.harrisburg.app.domain.ThreadDto;
import com.harrisburg.app.domain.ThreadMessage;

import java.util.List;
import java.util.UUID;

public interface ChatService {
    List<ThreadDto> getAllThreadsByUserId(Integer userId);

    //Load chat history by page: 20 message per page.
    List<ThreadMessage> getMessageFromThread(UUID threadId, Integer page);

    List<ThreadMessage> startChat(UUID threadId);

    List<ThreadMessage> startChat(Integer... userIds);
}
