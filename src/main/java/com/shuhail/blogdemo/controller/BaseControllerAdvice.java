package com.shuhail.blogdemo.controller;

import com.shuhail.blogdemo.domain.User;
import com.shuhail.blogdemo.exception.NotFoundException;
import com.shuhail.blogdemo.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class BaseControllerAdvice {
    private final UserService userService;

    public BaseControllerAdvice(UserService userService) {
        this.userService = userService;
    }

    @ExceptionHandler(NotFoundException.class)
    public String handledNotFoundException(NotFoundException e, Model model) {
        model.addAttribute("status", 400);
        model.addAttribute("exception", e);

        return "common/error";
    }

    @ExceptionHandler(Exception.class)
    public String handleException(Exception e, Model model) {
        model.addAttribute("status", 500);
        model.addAttribute("exception", e);

        return "common/error";
    }

    @ModelAttribute
    public  void addCommonAttributes(@AuthenticationPrincipal UserDetails userDetails, Model model){
        if (userDetails != null) {
            User user = userService.getByUsername(userDetails.getUsername());
            model.addAttribute("user", user);
        }
    }
}
