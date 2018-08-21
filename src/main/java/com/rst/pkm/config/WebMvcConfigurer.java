package com.rst.pkm.config;

import com.rst.pkm.controller.interceptor.GodCheckInterceptor;
import com.rst.pkm.controller.interceptor.RequestCheckInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 *
 * @author hujia
 * @date 2017/3/30
 */
@Configuration
public class WebMvcConfigurer extends WebMvcConfigurerAdapter {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new RequestCheckInterceptor()).addPathPatterns("/key/**");
        registry.addInterceptor(new GodCheckInterceptor()).addPathPatterns("/god/**");
    }
}
