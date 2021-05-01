package com.harrisburg.app.controller;

import com.harrisburg.app.domain.SearchFriendResponse;
import com.harrisburg.app.domain.ListContactResponse;
import com.harrisburg.app.domain.User;
import com.harrisburg.app.exception.UserNotFoundException;
import com.harrisburg.app.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@Slf4j
@RequestMapping("/chat")
public class ChatController {

    private final UserService userService;

    public ChatController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/search-friend")
    @ResponseBody
    public SearchFriendResponse searchFriend(@RequestParam(name = "username", required = true) String username, HttpSession session) {
        SearchFriendResponse searchFriendResponse = new SearchFriendResponse();

        String currentUsername = (String) session.getAttribute("username");
        if (currentUsername.equals(username)) {
            searchFriendResponse.setSuccess(false);
            searchFriendResponse.setMessage("No User Found!");
            return searchFriendResponse;
        }

        try {
            User user = userService.findUserByUsername(username);
            searchFriendResponse.setUser(user);
            searchFriendResponse.setSuccess(true);
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
}
