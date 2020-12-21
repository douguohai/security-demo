package com.example.securitydemo;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class MyAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Cache-Control", "no-cache");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        JSONObject successBean = new JSONObject();
        successBean.put("code",-2);
        successBean.put("msg","资源-访问被拒绝");
        response.getWriter().println(JSONUtil.toJsonStr(successBean));
        response.getWriter().flush();
    }
}
