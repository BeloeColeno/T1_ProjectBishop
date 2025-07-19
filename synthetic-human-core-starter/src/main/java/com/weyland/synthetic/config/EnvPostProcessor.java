package com.weyland.synthetic.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.env.PropertiesPropertySourceLoader;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;

public class EnvPostProcessor implements EnvironmentPostProcessor {
    private final PropertiesPropertySourceLoader propertySourceLoader;

    public EnvPostProcessor() {
        this.propertySourceLoader = new PropertiesPropertySourceLoader();
    }

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application){
        var resources = new ClassPathResource("default.properties");
        PropertySource<?> propertySource = null;
        try {
            propertySource = propertySourceLoader.load("synthetic-human-core-starter", resources).getFirst();
        } catch (Exception e) {
            throw new RuntimeException("Failed to load default properties", e);
        }
        environment.getPropertySources().addLast(propertySource);
    }
}
