package com.telran.groupservice.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class FeignConfig {

    @Bean
    public RequestInterceptor customHeaderInterceptor() {
        return (RequestTemplate template) -> {
            ServletRequestAttributes attributes = 
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

            if (attributes != null) {
                String customId = attributes.getRequest().getHeader("X-Request-ID");
                if (customId != null) {
                    template.header("X-Request-Id", customId);
                }
            }
        };
    }
}