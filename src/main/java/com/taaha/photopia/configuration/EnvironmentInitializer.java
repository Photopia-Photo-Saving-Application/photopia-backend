package com.taaha.photopia.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.WebApplicationInitializer;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;


//@Configuration
//@PropertySource("classpath:application.properties")
public class EnvironmentInitializer  implements WebApplicationInitializer {

    @Autowired
    private Environment env;

//    public String getProperty(String key) {
//        return env.getProperty(key);
//    }

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {

    }

//    @Override
//    public void setEnvironment(Environment environment) {
//        this.env=  environment;
//    }
}
