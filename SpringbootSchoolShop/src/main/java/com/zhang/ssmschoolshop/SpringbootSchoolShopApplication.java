package com.zhang.ssmschoolshop;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Properties;

@SpringBootApplication
@EnableSwagger2
@MapperScan("com.zhang.ssmschoolshop.dao")
public class SpringbootSchoolShopApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(SpringbootSchoolShopApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringbootSchoolShopApplication.class, args);
    }

    @Bean
    public JavaMailSender mailSender(
            @Value("${mail.host}") String host,
            @Value("${mail.port:465}") int port,
            @Value("${mail.username}") String username,
            @Value("${mail.password}") String password,
            @Value("${mail.default-encoding:UTF-8}") String defaultEncoding,
            @Value("${mail.protocol:smtp}") String protocol,
            @Value("${mail.properties.mail.smtp.ssl.enable:true}") boolean sslEnabled) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setPort(port);
        mailSender.setUsername(username);
        mailSender.setPassword(password);
        mailSender.setDefaultEncoding(defaultEncoding);
        mailSender.setProtocol(protocol);

        Properties properties = mailSender.getJavaMailProperties();
        properties.put("mail.smtp.auth", String.valueOf(username != null && !username.isEmpty()));
        properties.put("mail.smtp.ssl.enable", String.valueOf(sslEnabled));

        return mailSender;
    }
}
