package com.example.securitydemo.bean;

import lombok.Data;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * @version : 1.0
 * @description: java类作用描述
 * @author: tianwen
 * @create: 2021/1/1 10:44
 **/
@Configuration
public class MyBeanNameAware {

    @Data
    public static class Dou implements BeanNameAware, InitializingBean, DisposableBean {
        private String name;
        @Override
        public void setBeanName(String s) {
            System.out.println("Dou bean 名称: " + s);
            Dou dou = ApplicationContextHolder.getBean(s);
            dou.setName("孙子");
        }

        @Override
        public void afterPropertiesSet() throws Exception {
            System.out.println("Dou afterPropertiesSet 名称: " );
        }

        @Override
        public void destroy() throws Exception {
            System.out.println("Dou destroy 名称: " );
        }
    }

    @Bean
    public Dou dou() {
        Dou dou = new Dou();
        dou.name = "窦国海";
        return dou;
    }

}
