package com.example.securitydemo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        //开启httpbasic认证
//        http.formLogin()
//                .failureHandler(myAuthenticationFailureHandler())
//                .and()
//                .authorizeRequests()
//                .anyRequest()
//                .authenticated().and().exceptionHandling();
////                .accessDeniedHandler(myAccessDeniedHandler())
////                .authenticationEntryPoint(myAuthenticationEntryPoint());
////                .and().authenticationProvider(myAuthenticationProvider());//所有请求都需要登录认证才能访问/
//    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //开启httpbasic认证
        http.formLogin().and().authorizeRequests().antMatchers("/bean/**").permitAll();
    }

    @Bean
    MyAuthenticationFailureHandler myAuthenticationFailureHandler() {
        return new MyAuthenticationFailureHandler();
    }

    @Bean
    MyAccessDeniedHandler myAccessDeniedHandler() {
        return new MyAccessDeniedHandler();
    }

    @Bean
    MyAuthenticationEntryPoint myAuthenticationEntryPoint() {
        return new MyAuthenticationEntryPoint();
    }

//    @Bean
//    MyAuthenticationProvider myAuthenticationProvider(){
//        return new MyAuthenticationProvider();
//    }

//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.inMemoryAuthentication().withUser("user").password("1234");
//    }


}
