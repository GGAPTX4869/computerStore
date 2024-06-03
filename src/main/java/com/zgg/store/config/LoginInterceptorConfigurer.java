package com.zgg.store.config;

import com.zgg.store.interceptor.LoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration//将这个类注册到spring容器中
public class LoginInterceptorConfigurer implements WebMvcConfigurer {
    //将自定义的拦截器进行注册
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/web/index.html","/web/login.html","/web/register.html"
                        ,"/web/product.html","/users/reg","/users/login","/products/**","/index.html"
                        ,"/bootstrap3/**","/css/**","/images/**","/js/**");
    }
}
