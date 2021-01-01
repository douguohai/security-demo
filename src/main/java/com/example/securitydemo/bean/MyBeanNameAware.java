package com.example.securitydemo.bean;

import lombok.Data;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @version : 1.0
 * @description: java类作用描述
 * @author: tianwen
 * @create: 2021/1/1 10:44
 **/
@Configuration
public class MyBeanNameAware {

    @Data
    public static class Dou implements BeanNameAware {
        private String name;



        @Override
        public void setBeanName(String s) {
            System.out.println("Dou bean 名称: " + s);
            Dou dou=ApplicationContextHolder.getBean(s);
            dou.setName("孙子");
        }
    }

    @Bean
    public Dou dou() {
        Dou dou = new Dou();
        dou.name = "窦国海";
        return dou;
    }

}
