package org.einnfeigr.website.util;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.MethodParameter;
import org.springframework.mobile.device.Device;
import org.springframework.mobile.device.DeviceHandlerMethodArgumentResolver;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.LocaleResolver;

public class ArgumentResolver implements HandlerMethodArgumentResolver {

	private DeviceHandlerMethodArgumentResolver deviceResolver;
	private LocaleResolver localeResolver;
	
	public ArgumentResolver(DeviceHandlerMethodArgumentResolver deviceResolver, 
			LocaleResolver localeResolver) {
		this.deviceResolver = deviceResolver;
		this.localeResolver = localeResolver;
	}
	
	@Override
	public boolean supportsParameter(MethodParameter parameter) {
	    return parameter.getParameterType().equals(ControllerUtils.class);
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
		HttpServletRequest request = (HttpServletRequest)webRequest.getNativeRequest();
		HttpServletResponse response = (HttpServletResponse)webRequest.getNativeResponse();
		Locale locale = localeResolver.resolveLocale(request);
		Device device = (Device)deviceResolver.resolveArgument(parameter, mavContainer, webRequest,
				binderFactory);
		return new ControllerUtils(request, response, locale, device);
	}

}
