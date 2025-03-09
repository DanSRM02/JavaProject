package com.oxi.software.utilities;

import com.oxi.software.service.GeocodingService;
import lombok.Getter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class GeocodingServiceInjector implements ApplicationContextAware {
    @Getter
    private static GeocodingService geocodingService;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        geocodingService = applicationContext.getBean(GeocodingService.class);
    }

}