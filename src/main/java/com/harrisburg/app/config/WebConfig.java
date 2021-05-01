package com.harrisburg.app.config;

import com.harrisburg.app.filter.SecurityFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebConfig {

    @Bean
    public FilterRegistrationBean<SecurityFilter> securityFilter() {
        FilterRegistrationBean<SecurityFilter> registrationBean = new FilterRegistrationBean<>();

        registrationBean.setFilter(new SecurityFilter());
        registrationBean.addUrlPatterns("/chat/*");

        return registrationBean;
    }
}
