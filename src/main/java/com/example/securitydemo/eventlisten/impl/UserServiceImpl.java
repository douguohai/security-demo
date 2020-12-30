package com.example.securitydemo.eventlisten.impl;

import com.example.securitydemo.eventlisten.UserService;
import com.example.securitydemo.eventlisten.event.UserRegisterEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService, ApplicationEventPublisherAware {

    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher=applicationEventPublisher;
    }

    @Override
    public void pushMessage() {
        UserRegisterEvent userRegisterEvent=new UserRegisterEvent(this,"11111");
        applicationEventPublisher.publishEvent(userRegisterEvent);
    }
}
