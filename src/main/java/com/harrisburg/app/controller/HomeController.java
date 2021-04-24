package com.harrisburg.app.controller;

import com.harrisburg.app.entity.UserInfo;
import com.harrisburg.app.exception.InvalidUserCredentialException;
import com.harrisburg.app.exception.UserExistedException;
import com.harrisburg.app.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
@Slf4j
public class HomeController {

    private final UserService userService;

    public HomeController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("")
    public String home(Model model) {
        setDefaultLoginUser(model);
        setDefaultRegisterUser(model);
        model.addAttribute("leftActive", true);

        return "home";
    }
    
    @PostMapping("/signin")
    public String login(@Valid @ModelAttribute("loginUser") UserInfo loginUser, BindingResult bindingResult, Model model, HttpSession session) {

        model.addAttribute("leftActive", true);
        setDefaultRegisterUser(model);

        if (bindingResult.hasErrors()) {
            return "home";
        }

        try {
            UserInfo existedUser = userService.validateUser(loginUser);
            session.setAttribute("userId", existedUser.getId());
            session.setAttribute("firstname", existedUser.getFirstname());
            session.setAttribute("lastname", existedUser.getLastname());
        } catch (InvalidUserCredentialException e) {
            log.error(e.getMessage());
            model.addAttribute("userValidationError", e.getMessage());
            return "home";
        }

        return "chat";
    }

    @PostMapping("/signup")
    public String signup(@Valid @ModelAttribute("registerUser") UserInfo registerUser, BindingResult bindingResult, Model model) {

        model.addAttribute("leftActive", false);
        setDefaultLoginUser(model);

        if (bindingResult.hasErrors()) {
            return "home";
        }

        try {
            userService.addUser(registerUser);
            model.addAttribute("leftActive", true);
            model.addAttribute("registered", "Sign Up successfully. Please login");
        } catch (UserExistedException e) {
            log.error(e.getMessage());
            model.addAttribute("userExistedError", e.getMessage());
            return "home";
        }

        return "home";
    }

    private void setDefaultLoginUser(Model model) {
        UserInfo loginUser = UserInfo.builder()
                .username("")
                .password("")
                .build();

        model.addAttribute("loginUser", loginUser);
    }

    private void setDefaultRegisterUser(Model model) {
        UserInfo registerUser = UserInfo.builder()
                .username("")
                .password("")
                .firstname("")
                .lastname("")
                .phone("")
                .build();

        model.addAttribute("registerUser", registerUser);
    }
}
