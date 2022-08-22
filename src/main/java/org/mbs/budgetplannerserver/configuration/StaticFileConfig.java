package org.mbs.budgetplannerserver.configuration;

import org.mbs.budgetplannerserver.domain.BudgetStatusStringToEnumConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class StaticFileConfig implements WebMvcConfigurer {

    @Value("${budget-planner.static.path}")
    private String staticPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        WebMvcConfigurer.super.addResourceHandlers(registry);
        registry
                .addResourceHandler("/**")
                .addResourceLocations("file:" + staticPath + "/");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/")
                .setViewName("forward:/index.html");
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new BudgetStatusStringToEnumConverter());
    }
}
