package com.example.securitydemo.bean;

import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.context.annotation.Configuration;

/**
 * @version : 1.0
 * @description: java类作用描述
 * @author: tianwen
 * @create: 2021/1/1 11:57
 **/
@Configuration
public class MyBeanClassLoaderAware implements BeanClassLoaderAware {
    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        System.out.println(classLoader.getParent().getClass().getName());
    }
}
