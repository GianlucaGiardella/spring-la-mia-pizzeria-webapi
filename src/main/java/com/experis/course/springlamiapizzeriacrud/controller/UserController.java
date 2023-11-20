package com.experis.course.springlamiapizzeriacrud.controller;

import com.experis.course.springlamiapizzeriacrud.model.User;
import com.experis.course.springlamiapizzeriacrud.repository.UserRepository;
import com.experis.course.springlamiapizzeriacrud.security.DatabaseUserDatails;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/users")
public class UserController {
    @Autowired
    UserRepository userRepository;

    @GetMapping
    public String index(
            Authentication authentication,
            Model model
    ) {
        DatabaseUserDatails principal = (DatabaseUserDatails) authentication.getPrincipal();

        User loggerUser = userRepository.findById(principal.getId()).get();

        model.addAttribute("firstName", loggerUser.getFirstName());
        model.addAttribute("lastName", loggerUser.getLastName());

        return "users/index";
    }
}
