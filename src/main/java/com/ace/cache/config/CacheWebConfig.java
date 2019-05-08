package com.ace.cache.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

@Configuration("cacheWebConfig")
public class CacheWebConfig extends WebMvcConfigurationSupport {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/cache/**").addResourceLocations(
                "classpath:/META-INF/static/");
        super.addResourceHandlers(registry);
    }
}
