package org.einnfeigr.website;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;

import org.assertj.core.util.Lists;
import org.einnfeigr.website.manager.DropboxManager;
import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;


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
	private final static Parser parser = Parser.builder().build();
	private final static HtmlRenderer renderer = HtmlRenderer.builder().build();
	private final static Logger log = LoggerFactory.getLogger(WebsiteApplicationTests.class);
	
	public final static String MOBILE_USER_AGENT = "Mozilla/5.0 (Linux; Android 7.0; SM-G930V " 
			+ "Build/NRD90M) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.125 Mobile " 
			+ "Safari/537.36";
	
	private String hash;
	
	@BeforeEach
	void init() {
		hash = String.valueOf(mvc.hashCode());
	}
	
	@Test
	void checkPages() throws Exception {
		List<String> addresses = Lists.list("", "info", "fridrum", "about", "faq", "fridrum/notes",
				"fridrum/albums");
		for(String address : addresses) {
			mvc.perform(MockMvcRequestBuilders.get("/"+address))
				.andExpect(MockMvcResultMatchers.status().isOk());
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
		mvc.perform(MockMvcRequestBuilders.get("/fridrum/media/media/test.jpg"))
			.andExpect(MockMvcResultMatchers.status().isOk());
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
		try {
			mvc.perform(MockMvcRequestBuilders.get("/fridrum/albums/test"+hash))
				.andExpect(MockMvcResultMatchers.status().isNotFound());
			dropboxManager.createFolder("albums/test"+hash);
			mvc.perform(MockMvcRequestBuilders.get("/fridrum/albums/test"+hash))
				.andExpect(MockMvcResultMatchers.status().isOk());
		} finally {
			dropboxManager.delete("albums/test"+hash);
			mvc.perform(MockMvcRequestBuilders.get("/fridrum/albums/test"+hash))
			.andExpect(MockMvcResultMatchers.status().isOk());
			mvc.perform(MockMvcRequestBuilders.post("/api/cache/reset")
					.with(user("admin").roles("admin")))
				.andExpect(MockMvcResultMatchers.status().isOk());
			mvc.perform(MockMvcRequestBuilders.get("/fridrum/albums/test"+hash))
				.andExpect(MockMvcResultMatchers.status().isNotFound());
		}
	}

	@Test
	void createLocalizedAlbum() throws Exception {
		String path = "albums/test"+hash;
		Map<String, String> data = new HashMap<>();
		data.put("ru", "тест");
		data.put("en", "test");
		ObjectMapper mapper = new ObjectMapper();
		byte[] bytes = mapper.writeValueAsBytes(data);
		try {
			mvc.perform(MockMvcRequestBuilders.get("/fridrum/"+path))
				.andExpect(MockMvcResultMatchers.status().isNotFound());
			dropboxManager.createFolder(path);
			dropboxManager.writeFile(path+"/locales.properties", bytes);
			mvc.perform(MockMvcRequestBuilders.get("/fridrum/"+path))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().string(containsString("test")));
			mvc.perform(MockMvcRequestBuilders.get("/fridrum/"+path).param("lang", "ru"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().string(containsString("тест")));
		} finally {
			dropboxManager.delete(path);
		}
	}
	
	@Test
	void createNotes() throws Exception {
		String filename = "test"+hash;
		String path = "notes/%s/%s.md";
		createAndTestNote(filename, String.format(path, "en", filename), "# Test", "en");
		createAndTestNote(filename, String.format(path, "ru", filename), "# Тест", "ru");		
	}
	
	void createAndTestNote(String name, String path, String content, String lang) throws Exception {
		String output = renderer.render(parser.parse(content));
		try {
			mvc.perform(MockMvcRequestBuilders.get("/fridrum/notes/"+name).param("lang", lang))
				.andExpect(MockMvcResultMatchers.status().isNotFound());			
			dropboxManager.writeFile(path, content.getBytes());
			mvc.perform(MockMvcRequestBuilders.get("/fridrum/notes/"+name).param("lang", lang))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().string(containsString(output)));
			mvc.perform(MockMvcRequestBuilders.get("/fridrum/notes/"+name+".md")
					.param("lang", lang))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().string(containsString(output)));
		} finally {
			dropboxManager.delete(path);
			mvc.perform(MockMvcRequestBuilders.get("/fridrum/notes/"+name).param("lang", lang))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().string(containsString(output)));
			mvc.perform(MockMvcRequestBuilders.post("/api/cache/reset")
					.with(user("admin").roles("admin")))
				.andExpect(MockMvcResultMatchers.status().isOk());
			mvc.perform(MockMvcRequestBuilders.get("/fridrum/notes/"+name).param("lang", lang))
				.andExpect(MockMvcResultMatchers.status().isNotFound());			
		}
	}
	
}
