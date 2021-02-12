package org.einnfeigr.website;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.mobile.device.Device;
import org.springframework.web.servlet.ModelAndView;

public class ControllerUtils {

	private static final List<String> availableLangs = new ArrayList<>();
	
	
	public static final String LIGHT_THEME_NAME = "light";
	public static final String DARK_THEME_NAME = "dark";
	
	public static final String DEFAULT_THEME = LIGHT_THEME_NAME;
	public static final String DEFAULT_VERSION = "desktop";
	
	public static final String THEME_COOKIE_NAME = "theme";
	public static final String VERSION_COOKIE_NAME = "ver";
	
	public static final String THEME_PARAM_NAME = "theme";
	public static final String VERSION_PARAM_NAME = "ver";
	
	private HttpServletRequest request;
	private HttpServletResponse response;
	private Locale locale;
	private Device device;
	
	static {
		availableLangs.add("ru");
		availableLangs.add("en");
	}

	public ControllerUtils(HttpServletRequest request, HttpServletResponse response, Locale locale, 
			Device device) {
		this.request = request;
		this.response = response;
		this.locale = locale;
		this.device = device;
	}
	
	public static List<String> getAvailableLangs() {
		return availableLangs;
	}
	
	//Selected language must be displayed first in list
	public static List<String> getSortedLangs(Locale locale) {
		List<String> langs = new ArrayList<>(availableLangs);
		langs.remove(locale.getLanguage());
		langs.add(0, locale.getLanguage());
		return langs;
	}
	
	public ModelAndView buildMav(String path) throws FileNotFoundException {
		return buildMav(path, locale, device, request, response, "");
	}
	
	public ModelAndView buildMav(String path, Object...params) throws FileNotFoundException {
		String theme = parseTheme(request, response);
		String version = parseVersion(request, response);
		ModelAndView mav = new ModelAndView(path);
		Map<String, Object> data = mav.getModel();
		data.put("locale", locale.getLanguage());
		data.put("isMobile", !device.isNormal() || "mobile".equals(version));
		data.put("langs", getSortedLangs(locale));
		data.put("availableTheme", theme.equals(DARK_THEME_NAME) 
				? LIGHT_THEME_NAME : DARK_THEME_NAME);
		data.put("theme", theme.equals(DARK_THEME_NAME) ? theme : LIGHT_THEME_NAME);
		data.put("page", path);
		data.putAll(arrayToMap(params));
		return mav;
	}
	
	private static String parseVersion(HttpServletRequest request, HttpServletResponse response) {
		String version;
		if((version = request.getParameter(VERSION_PARAM_NAME)) != null) {
			response.addCookie(new Cookie(VERSION_COOKIE_NAME, version));
			return version;
		}
		if(request.getCookies() == null) {
			return DEFAULT_VERSION;
		}
		for(Cookie cookie : request.getCookies()) {
			if(cookie.getName().equals(VERSION_COOKIE_NAME)) {
				return cookie.getValue();
			}
		}
		return DEFAULT_VERSION;
	}
	
	private static String parseTheme(HttpServletRequest request, HttpServletResponse response) {
		String theme;
		if((theme = request.getParameter(THEME_PARAM_NAME)) != null) {
			response.addCookie(new Cookie(THEME_COOKIE_NAME, theme));
			return theme;
		}
		if(request.getCookies() == null) {
			return DEFAULT_THEME;
		}
		for(Cookie cookie : request.getCookies()) {
			if(cookie.getName().equals(THEME_COOKIE_NAME)) {
				return cookie.getValue();
			}
		}
		return DEFAULT_THEME;
	}
	
	private static Map<String, Object> arrayToMap(Object[] params) {
		Map<String, Object> map = new HashMap<>();
		if(params.length > 0) {
			for(int x = 1; x < params.length; ++x) {
				map.put(params[x-1].toString(), params[x]);
			}
		}
		return map;
	}
	
}
