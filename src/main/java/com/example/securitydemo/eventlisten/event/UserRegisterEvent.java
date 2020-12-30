package com.example.securitydemo.eventlisten.event;

import org.springframework.context.ApplicationEvent;

public class UserRegisterEvent extends ApplicationEvent {
    private static final long serialVersionUID = -5481658020206295565L;

    private String key;

    public UserRegisterEvent(Object source,String key) {
        super(source);
        this.key=key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

}
