package com.example.securitydemo.bean;

import org.redisson.Redisson;
import org.redisson.config.SingleServerConfig;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Configuration;

/**
 * @version : 1.0
 * @description: java类作用描述
 * @author: tianwen
 * @create: 2021/1/1 12:01
 **/
@Configuration
public class MyBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if ("lockRedissonClient".equals(beanName)) {
            Redisson redisson = (Redisson) bean;
            org.redisson.config.Config config = redisson.getConfig();
            SingleServerConfig singleServerConfig = config.useSingleServer();
            singleServerConfig.setAddress(String.format("%s%s%s%s", "redis://", "127.0.0.1", ":", 6379));
            ((Redisson) bean).shutdown();
            bean = Redisson.create(config);
            System.out.println(bean.getClass().getSimpleName());
            System.out.println(beanName);
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if ("lockRedissonClient".equals(beanName)) {
            System.out.println(beanName);
        }
        System.out.println(beanName);
        return bean;
    }
}
