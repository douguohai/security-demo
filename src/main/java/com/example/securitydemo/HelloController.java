package com.example.securitydemo;

import com.example.securitydemo.eventlisten.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @Autowired
    private UserService userService;

    @RequestMapping("/")
    public String hello(){
        assert 1==1;
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication=securityContext.getAuthentication();
        userService.pushMessage();
        return "HELLO";
    }

    @RequestMapping("/a")
    @PreAuthorize("hasAnyRole('role_1')")
    public String a(){
        assert 1==1;
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication=securityContext.getAuthentication();
        return "HELLO";
    }
}
