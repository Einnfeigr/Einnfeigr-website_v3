package org.einnfeigr.website.config;

import java.util.List;
import java.util.Locale;

import org.einnfeigr.website.controller.ErrorController;
import org.einnfeigr.website.manager.CachedDropboxManager;
import org.einnfeigr.website.manager.CmsManager;
import org.einnfeigr.website.util.ArgumentResolver;
import org.einnfeigr.website.util.FileMarkdownHelper;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.mobile.device.DeviceHandlerMethodArgumentResolver;
import org.springframework.mobile.device.DeviceResolverHandlerInterceptor;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.files.ListFolderErrorException;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import com.github.jknack.handlebars.springmvc.HandlebarsViewResolver;

@SpringBootConfiguration
public class ApplicationConfig implements WebMvcConfigurer {

	private static HandlebarsViewResolver handlebars = null;

	@Bean
	public LocaleResolver localeResolver() {
		CookieLocaleResolver resolver = new CookieLocaleResolver();
		resolver.setDefaultLocale(Locale.US);
		return resolver;
	}
	
	@Bean
	public CmsManager cmsManager() throws ListFolderErrorException, DbxException {
		return new CachedDropboxManager();
	}
	
	@Bean
	public LocaleChangeInterceptor localeChangeInterceptor() {
		LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
		interceptor.setParamName("lang");
		return interceptor;
	}
	
    @Bean
    public DeviceResolverHandlerInterceptor deviceResolverHandlerInterceptor() { 
    	return new DeviceResolverHandlerInterceptor(); 
    }
	 
    @Bean
    public DeviceHandlerMethodArgumentResolver deviceHandlerMethodArgumentResolver() { 
        return new DeviceHandlerMethodArgumentResolver(); 
    }
 
    @Override
    public void addInterceptors(InterceptorRegistry registry) { 
        registry.addInterceptor(deviceResolverHandlerInterceptor()); 
        registry.addInterceptor(localeChangeInterceptor());
    }
 
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(deviceHandlerMethodArgumentResolver()); 
        argumentResolvers.add(new ArgumentResolver(deviceHandlerMethodArgumentResolver(), 
        		localeResolver())); 
    }
    
	@Bean
	public HandlebarsViewResolver handlebars() {
		ClassPathTemplateLoader loader = new ClassPathTemplateLoader("/templates", ".hbs");
		Handlebars hbs = new Handlebars(loader);
		handlebars = new HandlebarsViewResolver(hbs);
		handlebars.registerHelper("md", new FileMarkdownHelper());
		handlebars.setCache(false);
		return handlebars;
	}
	
}
