package org.einnfeigr.website;

import static org.junit.Assert.assertTrue;

import org.einnfeigr.website.controller.view.MediaController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.spec.internal.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;


@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.DisplayName.class)
@DisplayName("Einnfeigr website application tests")
class WebsiteApplicationTests {
	
	@Autowired
	private MockMvc mvc;

	@Test
	void checkPagesAvailability() throws Exception {
		assertTrue(checkAvailability("/").getResponse().getStatus() == HttpStatus.OK 
				&& checkAvailability("/info").getResponse().getStatus() == HttpStatus.OK
				&& checkAvailability("/about").getResponse().getStatus() == HttpStatus.OK);
	}
	
	private MvcResult checkAvailability(String path) throws Exception {
		return mvc.perform(MockMvcRequestBuilders.get(path)).andReturn();
	}
	
	@Test
	void test404() throws Exception {
		assertTrue(checkAvailability("/404").getResponse().getStatus() == HttpStatus.NOT_FOUND);
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
