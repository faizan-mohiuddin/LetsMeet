// package com.LetsMeet.LetsMeet.Utilities.Config;

// import java.nio.charset.StandardCharsets;

// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.thymeleaf.spring5.SpringTemplateEngine;
// import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
// import org.thymeleaf.templatemode.TemplateMode;

// /**
//  * Configuration for email specific template processing
//  * @author Hamish Weir
//  */
// @Configuration
// public class ThymleafEmailConfig {

//     @Bean
//     public SpringTemplateEngine springTemplateEngine() {
//         SpringTemplateEngine templateEngine = new SpringTemplateEngine();
//         templateEngine.addTemplateResolver(htmlTemplateResolver());
//         return templateEngine;
//     }

//     @Bean
//     public SpringResourceTemplateResolver htmlTemplateResolver(){
//         SpringResourceTemplateResolver emailTemplateResolver = new SpringResourceTemplateResolver();
//         emailTemplateResolver.setPrefix("/templates/mail/");
//         emailTemplateResolver.setSuffix(".html");
//         emailTemplateResolver.setTemplateMode(TemplateMode.HTML);
//         emailTemplateResolver.setCharacterEncoding(StandardCharsets.UTF_8.name());
//         return emailTemplateResolver;
//     }
// }
