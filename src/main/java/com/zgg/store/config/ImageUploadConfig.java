package com.zgg.store.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ImageUploadConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//映射图片保存地址
        registry.addResourceHandler("/static/images/upload/**").addResourceLocations("file:E:\\Allworkspaces\\idea-workspace\\SpringBoot\\store\\src\\main\\resources\\static\\images\\upload\\");
    }
}
