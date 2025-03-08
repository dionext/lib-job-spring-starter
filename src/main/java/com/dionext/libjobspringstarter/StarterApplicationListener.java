package com.dionext.libjobspringstarter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.List;

@Slf4j
public class StarterApplicationListener implements ApplicationListener<ApplicationEvent> {
    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ApplicationEnvironmentPreparedEvent) {
            ApplicationEnvironmentPreparedEvent envEvent = (ApplicationEnvironmentPreparedEvent) event;
            ConfigurableEnvironment env = envEvent.getEnvironment();

            //https://stackoverflow.com/questions/21271468/spring-propertysource-using-yaml
            Resource resource = new ClassPathResource("application-libjobspringstarter.yaml");
            YamlPropertySourceLoader sourceLoader = new YamlPropertySourceLoader();
            try {
                List<PropertySource<?>> yamlTestProperties = sourceLoader.load("libjobspringstarterProperties", resource);
                env.getPropertySources()
                        .addLast(yamlTestProperties.get(0));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}