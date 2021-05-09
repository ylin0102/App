package com.harrisburg.app.controller;

import com.harrisburg.app.domain.*;
import com.harrisburg.app.exception.UserNotFoundException;
import com.harrisburg.app.service.ChatService;
import com.harrisburg.app.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.UUID;

@Controller
@Slf4j
@RequestMapping("/chat")
public class ChatController {

    private final UserService userService;
    private final ChatService chatService;

    public ChatController(UserService userService, ChatService chatService) {
        this.userService = userService;
        this.chatService = chatService;
    }

    @GetMapping("/search-friend")
    @ResponseBody
    public SearchFriendResponse searchFriend(@RequestParam(name = "username") String username, HttpSession session) {
        SearchFriendResponse searchFriendResponse = new SearchFriendResponse();

        String currentUsername = (String) session.getAttribute("username");
        if (currentUsername.equals(username)) {
            searchFriendResponse.setSuccess(false);
            searchFriendResponse.setMessage("No User Found!");
            return searchFriendResponse;
        }

        try {
            User user = userService.findUserByUsername(username);
            Integer currentUserId = (Integer) session.getAttribute("userId");
            Boolean isNewContact = userService.isOldFriend(currentUserId, user.getId());
            searchFriendResponse.setUser(user);
            searchFriendResponse.setSuccess(true);
            searchFriendResponse.setNew(!isNewContact);
        } catch (UserNotFoundException e) {
            log.error(e.getMessage());
            searchFriendResponse.setSuccess(false);
            searchFriendResponse.setMessage(e.getMessage());
        }

        return searchFriendResponse;
    }

    @GetMapping("/list-contacts")
    @ResponseBody
    public ListContactResponse listContacts(HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");

        List<User> contacts = userService.getAllContactByUserId(userId);

        return ListContactResponse.builder()
                .success(true)
                .contacts(contacts)
                .build();
    }

    @GetMapping("/add-friend")
    @ResponseBody
    public GetMessageFromThreadResponse addFriend(@RequestParam(name = "userId") Integer userId, HttpSession session) {
        Integer currentUserId = (Integer) session.getAttribute("userId");
        GetMessageFromThreadResponse response = new GetMessageFromThreadResponse();

        if (currentUserId.equals(userId)) {
            response.setSuccess(false);
            response.setMessage("Invalid User Id: Users can not add themselves as contacts");
            return response;
        }

        userService.addContact(userId, currentUserId);
        List<ThreadMessage> threadMessages = chatService.startChat(userId, currentUserId);

        response.setThreadId((UUID) session.getAttribute("threadId"));
        response.setThreadName((String) session.getAttribute("threadName"));
        response.setUpdatedAt((String) session.getAttribute("updatedAt"));
        response.setUpdatedDate((String) session.getAttribute("updatedDate"));
        response.setSuccess(true);
        response.setMessages(threadMessages);
        return response;
    }

    @GetMapping("/load-user-threads")
    @ResponseBody
    public GetAllUserThreadsResponse loadUserThreads(HttpSession session) {
        Integer currentUserId = (Integer) session.getAttribute("userId");

        return GetAllUserThreadsResponse.builder()
                .success(true)
                .threads(chatService.getAllThreadsByUserId(currentUserId))
                .build();
    }

    @GetMapping("/load-thread")
    @ResponseBody
    public GetMessageFromThreadResponse loadThreadWithThreadId(@RequestParam(name = "threadId") UUID threadId, @RequestParam(name = "page") Integer page) {
        if (page == null) {
            page = 1;
        }

        return GetMessageFromThreadResponse.builder()
                .success(true)
                .messages(chatService.getMessageFromThread(threadId, page))
                .build();
    }

    @PostMapping("/load-thread")
    @ResponseBody
    public GetMessageFromThreadResponse loadThreadWithUserIds(@RequestBody GetMessageFromThreadRequest request, HttpSession session) {
        Integer currentUserId = (Integer) session.getAttribute("userId");
        GetMessageFromThreadResponse response = new GetMessageFromThreadResponse();

        List<Integer> userIds = request.getUserIds();
        userIds.add(currentUserId);
        List<ThreadMessage> threadMessages = chatService.startChat(userIds.toArray(new Integer[0]));

        response.setThreadId((UUID) session.getAttribute("threadId"));
        response.setSuccess(true);
        response.setMessages(threadMessages);
        return response;
    }
}
