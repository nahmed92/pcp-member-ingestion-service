package com.delta.pcpingestion.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.stereotype.Component;

@Configuration
@EnableRetry
@Component
@PropertySource("classpath:application.properties")
public class AppConfig {

}
