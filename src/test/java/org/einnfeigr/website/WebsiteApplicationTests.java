package org.einnfeigr.website;

import static org.junit.Assert.assertTrue;

import java.util.List;

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
import org.springframework.cloud.contract.spec.internal.HttpStatus;
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

	private final static Logger log = LoggerFactory.getLogger(WebsiteApplicationTests.class);
	public final static String MOBILE_USER_AGENT = "Mozilla/5.0 (Linux; Android 7.0; SM-G930V " 
			+ "Build/NRD90M) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.125 Mobile " 
			+ "Safari/537.36";
	
	@Test
	void checkPagesAvailability() throws Exception {
		List<String> addresses = Lists.list("", "info", "fridrum", "about", "faq");
		List<String> themes = Lists.list("dark", "light");
		List<String> langs = Lists.list("ru", "en");
		boolean isPassed = checkAddresses(addresses);
		for(int x = 0; x < 2; x++) {
			for(String lang : langs) {
				performGet("/?lang="+lang);
				for(String theme : themes) {
					performGet("/?theme="+theme);
					isPassed = checkAddresses(addresses) && isPassed;
				}
			}
			performGet("/", true);
		}
		assertTrue(isPassed);
	}
	
	private boolean checkAddresses(List<String> addresses) throws Exception {
		boolean isPassed = true;
		boolean isAvailable;
		for(String address : addresses) {
			address = "/"+address;
			isAvailable = performGet(address).getResponse().getStatus() == HttpStatus.OK; 
			log.info(address+"\t:\t"+(isAvailable ? "" : "not") + "available");
			isPassed = isAvailable && isPassed;
		}
		return isPassed;
	}
	
	private MvcResult performGet(String path) throws Exception {
		return performGet(path, false);
	}
	
	private MvcResult performGet(String path, boolean isMobile) throws Exception {
		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(path);
		if(isMobile) {
			builder.header("user-agent", MOBILE_USER_AGENT);
		}
		return mvc.perform(builder).andReturn();
	}
	
	@Test
	void test404() throws Exception {
		assertTrue(performGet("/404").getResponse().getStatus() == HttpStatus.NOT_FOUND);
	}
	
	@Test
	void downloadMedia() throws Exception {
		MediaController.CACHE_AGE_SECONDS = 1;
		for(int x = 0; x < 3; x++) { 
			mvc.perform(MockMvcRequestBuilders.get("/fridrum/media/test.jpg"))
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
	
}
