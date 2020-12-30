package com.example.securitydemo.eventlisten;

import com.example.securitydemo.eventlisten.event.UserRegisterEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class EventListener implements ApplicationListener<UserRegisterEvent> {
    @Override
    public void onApplicationEvent(UserRegisterEvent userRegisterEvent) {
        System.out.println("我监听到了事件 ："+userRegisterEvent.getKey());
    }
}
