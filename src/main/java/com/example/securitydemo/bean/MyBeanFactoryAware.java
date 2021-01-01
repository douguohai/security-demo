package com.example.securitydemo.bean;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.alibaba.nacos.spring.context.annotation.config.NacosPropertySource;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;

/**
 * @version : 1.0
 * @description: java类作用描述
 * @author: tianwen
 * @create: 2021/1/1 11:26
 **/
@Configuration
@NacosPropertySource(dataId = "escortsys", autoRefreshed = true)
public class MyBeanFactoryAware implements BeanFactoryAware {

    @NacosValue(value = "${wx.pay.key}", autoRefreshed = true)
    private String useLocalCache;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        System.out.println(useLocalCache);
        MyBeanNameAware.Dou dou = (MyBeanNameAware.Dou) beanFactory.getBean("dou");
        Assert.notNull(dou, "是否为空");
    }
}
