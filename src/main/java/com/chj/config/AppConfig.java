package com.chj.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
@PropertySource(value = { "classpath:application.properties" })
@EnableAspectJAutoProxy 
public class AppConfig extends WebMvcConfigurerAdapter {

	@Autowired
	private Environment env;

}
