//-----------------------------------------------------------------
// SpringConfiguration.java
// Let's Meet 2021
//
// Configuration options for Spring MVC

package com.LetsMeet.LetsMeet.Utilities.Config;

import java.nio.file.Path;
import java.nio.file.Paths;

import com.LetsMeet.LetsMeet.Utilities.LetsMeetConfiguration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
 
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
        registry.addResourceHandler("/" + "media" + "/**").addResourceLocations("file:/"+ uploadPath + "/");
    }
}