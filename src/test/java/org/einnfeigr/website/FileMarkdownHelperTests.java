package org.einnfeigr.website;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;

public class FileMarkdownHelperTests {

	private static Handlebars hbs = new Handlebars();
	private final static String HELPER_PATTERN = "{{md \"%s\" }}";
	private Template template;
	private Map<String, String> params = new HashMap<>();
	
	@BeforeAll
	static void initHbs() {
		hbs.registerHelper("md", new FileMarkdownHelper());
	}
	
	@BeforeEach
	void initParams() {
		params.put("locale", "en");
	}
	
	@Test
	void testCompile() throws IOException {
		String output = "<h1>Test</h1>";
		template = hbs.compileInline(String.format(HELPER_PATTERN, "test"));
		assertEquals(output, template.apply(params));	
	}
	
	@Test
	void testInvalidPath() throws IOException {
		template = hbs.compileInline(String.format(HELPER_PATTERN, "missing"));
		assertEquals("", template.apply(params));
	}
	
	@Test
	void testNoLocale() throws IOException {
		template = hbs.compileInline(String.format(HELPER_PATTERN, "test"));
		assertEquals("", template.apply(null));
	}
	
	@Test
	void testPathFormatting() throws IOException {
		String output = "<h1>Test</h1>";
		String[] names = new String[] { "test.md", "/test", "test.txt", "/test.txt" };
		for(String name : names) {
			template = hbs.compileInline(String.format(HELPER_PATTERN, name));
			assertEquals(output, template.apply(params));
		}
	}
	
	@Test
	void testLocale() throws IOException {
		String outputEn = "<h1>Test</h1>";
		String outputRu = "<h1>Тест</h1>";
		template = hbs.compileInline(String.format(HELPER_PATTERN, "test"));
		params.put("locale", "en");
		assertEquals(outputEn, template.apply(params));	
		params.put("locale", "ru");
		assertEquals(outputRu, template.apply(params));			
	}
	
	@Test
	void testHandlebarsInMarkdown() throws IOException {
		String output = "<h1>Test</h1>";
		String text = "{{md \"testhbs.md\" }}";
		params.put("text", "Test");
		assertEquals(output, hbs.compileInline(text).apply(params));
	}
}
