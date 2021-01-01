package com.example.securitydemo.nacos;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.alibaba.nacos.spring.context.annotation.config.NacosPropertySource;
import io.gitee.tooleek.lock.spring.boot.annotation.Lock;
import io.gitee.tooleek.lock.spring.boot.enumeration.LockType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @version : 1.0
 * @description: java类作用描述
 * @author: tianwen
 * @create: 2020/12/31 09:09
 **/
@NacosPropertySource(dataId = "escortsys", autoRefreshed = true)
@RestController
public class NacosConfig {

    @NacosValue(value = "${wx.pay.key}", autoRefreshed = true)
    private String useLocalCache;

    @NacosValue(value = "${wx.pay.cert}", autoRefreshed = true)
    private String cert;

    @RequestMapping(value = "/get")
    @ResponseBody
    public boolean get() {
        System.out.printf(useLocalCache);
        System.out.printf(cert);
        return true;
    }
}
