package org.einnfeigr.website;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.servlet.http.Cookie;

import org.assertj.core.util.Lists;
import org.einnfeigr.website.controller.view.MediaController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;


@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.DisplayName.class)
@DisplayName("Einnfeigr website application tests")
class WebsiteApplicationTests {
	
	@Autowired
	private MockMvc mvc;

	@Autowired
	private DropboxManager dropboxManager;
	
	private final static String HOME_ADDRESS = "/";
	private final static String MOBILE_CHECK_TEXT = "<meta name=\"viewport";
	
	private final static Logger log = LoggerFactory.getLogger(WebsiteApplicationTests.class);
	public final static String MOBILE_USER_AGENT = "Mozilla/5.0 (Linux; Android 7.0; SM-G930V " 
			+ "Build/NRD90M) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.125 Mobile " 
			+ "Safari/537.36";
	
	@Test
	void checkPages() {
		List<String> addresses = Lists.list("", "info", "fridrum", "about", "faq", "fridrum/notes",
				"fridrum/albums");
		for(String address : addresses) {
			try {
				mvc.perform(MockMvcRequestBuilders.get(address))
					.andExpect(MockMvcResultMatchers.status().isOk());
			} catch (Exception e) {
				log.error("Error accessing "+address+"page", e);
			}
		}
	}
	
	@Test
	void checkThemes() throws Exception {
		Cookie[] cookies;
		MvcResult result;
		String address = "/";
		String text = "<body class=\"%s\"";
		checkContains(address, String.format(text, "light"));
		result = checkContains(address+"?theme=dark", String.format(text, "dark"));
		cookies = result.getResponse().getCookies();
		assertTrue(result.getResponse()
				.getCookie(ControllerUtils.THEME_COOKIE_NAME)
				.getValue()
				.equals("dark"));
		checkContains(address, String.format(text, "dark"), cookies);
		result = checkContains(address+"?theme=light", String.format(text, "light"));
		cookies = result.getResponse().getCookies();
		assertTrue(result.getResponse()
				.getCookie(ControllerUtils.THEME_COOKIE_NAME)
				.getValue()
				.equals("light"));
		checkContains(address, String.format(text, "light"), cookies);
	}

	@Test
	void checkLangs() throws Exception {
		String address = "/";
		checkContains(address, "lang=en");
		checkContains(address+"?lang=ru", "lang=ru");
		checkContains(address, "lang=ru");
		checkContains(address+"?lang=en", "lang=en");
		checkContains(address, "lang=en");
	}
	
	@Test
	void checkUserAgentSwitch() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get(HOME_ADDRESS))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.content()
					.string(not(containsString(MOBILE_CHECK_TEXT))));
		mvc.perform(MockMvcRequestBuilders.get(HOME_ADDRESS)
				.header("user-agent", MOBILE_USER_AGENT))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.content().string(containsString(MOBILE_CHECK_TEXT)));
	}
	
	@Test
	void checkVersionPropertySwitch() throws Exception {
		MvcResult result;
		mvc.perform(MockMvcRequestBuilders.get(HOME_ADDRESS))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.content()
					.string(not(containsString(MOBILE_CHECK_TEXT))));
		result = mvc.perform(MockMvcRequestBuilders.get(HOME_ADDRESS+"?ver=mobile"))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.content().string(containsString(MOBILE_CHECK_TEXT)))
			.andReturn();
		mvc.perform(MockMvcRequestBuilders.get(HOME_ADDRESS)
				.cookie(result.getResponse().getCookies()))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.content().string(containsString(MOBILE_CHECK_TEXT)));
		result = mvc.perform(MockMvcRequestBuilders.get(HOME_ADDRESS+"?ver=desktop"))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.content()
					.string(not(containsString(MOBILE_CHECK_TEXT))))
			.andReturn();
		mvc.perform(MockMvcRequestBuilders.get(HOME_ADDRESS)
				.cookie(result.getResponse().getCookies()))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.content()
					.string(not(containsString(MOBILE_CHECK_TEXT))));
	}
	
	MvcResult checkContains(String address, String text) throws Exception {
		return mvc.perform(MockMvcRequestBuilders.get(address))
				.andExpect(MockMvcResultMatchers.content()
						.string(containsString(text)))
				.andReturn();
	}
	
	MvcResult checkContains(String address, String text, Cookie... cookies) throws Exception {
		return mvc.perform(MockMvcRequestBuilders.get(address).cookie(cookies))
			.andExpect(MockMvcResultMatchers.content()
					.string(containsString(text)))
			.andReturn();
	}
	
	@Test
	void test404() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/notexistingpage"))
			.andExpect(MockMvcResultMatchers.status().isNotFound());
	}
	
	@Test
	void downloadMedia() throws Exception {
		MediaController.CACHE_AGE_SECONDS = 1;
		for(int x = 0; x < 3; x++) { 
			mvc.perform(MockMvcRequestBuilders.get("/fridrum/media/media/test.jpg"))
				.andExpect(MockMvcResultMatchers.status().isOk());
			if(x == 2) {
				Thread.sleep(6000);
			}
		}
	}
	
	@Test
	void downloadMediaError() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/fridrum/media/a.txt"))
			.andExpect(MockMvcResultMatchers.status().isNotFound());
	}
	
	@Test
	void testAlbums() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/fridrum/albums"))
			.andExpect(MockMvcResultMatchers.status().isOk());
	}
	
	@Test
	void createAlbum() throws Exception {
		String hash = ""+mvc.hashCode();
		try {
		
			dropboxManager.createFolder("albums/test"+hash);
			mvc.perform(MockMvcRequestBuilders.get("/fridrum/albums/test"+hash))
				.andExpect(MockMvcResultMatchers.status().isOk());
		} finally {
			dropboxManager.delete("albums/test"+hash);
			mvc.perform(MockMvcRequestBuilders.get("/fridrum/albums/test"+hash))
				.andExpect(MockMvcResultMatchers.status().isNotFound());
		}
	}
	
}
