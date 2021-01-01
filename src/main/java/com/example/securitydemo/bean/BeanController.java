package com.example.securitydemo.bean;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import io.gitee.tooleek.lock.spring.boot.annotation.Lock;
import io.gitee.tooleek.lock.spring.boot.enumeration.LockType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author tianwen
 */
@RestController
@RequestMapping(value = "/bean")
public class BeanController {

    @Autowired
    private MyBeanNameAware.Dou dou;

    @RequestMapping("/beanName")
    @Lock(lockType = LockType.FAIR, leaseTime = 30, waitTime = 30)
    public String beanName() {
        System.out.println(dou.getName());
        ApplicationContextHolder.getBean("lockConfig");
        return "HELLO";
    }
}
