//-----------------------------------------------------------------
// SpringConfiguration.java
// Let's Meet 2021
//
// Configuration options for Spring MVC

package com.LetsMeet.LetsMeet.Utilities.Config;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

import com.LetsMeet.LetsMeet.Utilities.LetsMeetConfiguration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

@Configuration
public class SpringConfiguration implements WebMvcConfigurer {

    @Autowired
    LetsMeetConfiguration config;
 
    /* Expose user upload directory */ 

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        exposeDirectory(config.getdataFolder(), registry);
    }
     
    private void exposeDirectory(String dirName, ResourceHandlerRegistry registry) {
        Path uploadDir = Paths.get(dirName);
        String uploadPath = uploadDir.toFile().getAbsolutePath();
         
        if (dirName.startsWith("../")) dirName = dirName.replace("../", "");
        registry.addResourceHandler("/" + "media" + "/**").addResourceLocations("/" + uploadPath +"/","file:///"+ uploadPath + "/");
    }

    /* Resolve users locale */

    @Bean
    public LocaleResolver localeResolver(){
        SessionLocaleResolver localeResolver = new SessionLocaleResolver();
        localeResolver.setDefaultLocale(Locale.US);
        return  localeResolver;
    }
    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor(){
        LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
        localeChangeInterceptor.setParamName("lang");
        return localeChangeInterceptor;
    }
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }
}