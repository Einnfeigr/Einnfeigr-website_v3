package org.einnfeigr.website;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.einnfeigr.website.util.FileMarkdownHelper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;

@DisplayName("FileMarkdownHelper class tests")
public class FileMarkdownHelperTests {

	private static Handlebars hbs = new Handlebars();
	private final static String HELPER_PATTERN = "{{md \"%s\" }}";
	private Template template;
	private Map<String, String> params = new HashMap<>();
	private final static Parser parser = Parser.builder().build();
	private final static HtmlRenderer renderer = HtmlRenderer.builder().build();
	
	@BeforeAll
	static void initHbs() {
		hbs.registerHelper("md", new FileMarkdownHelper());
	}
	
	@BeforeEach
	void initParams() {
		params.put("locale", "en");
	}
	
	@DisplayName("Compile simple file")
	@Test
	void testCompile() throws IOException {
		String output = renderer.render(parser.parse("# Test"));
		template = hbs.compileInline(String.format(HELPER_PATTERN, "test"));
		assertEquals(output, template.apply(params));	
	}
	
	@DisplayName("Try to compile non existing file")
	@Test
	void testInvalidPath() throws IOException {
		template = hbs.compileInline(String.format(HELPER_PATTERN, "missing"));
		assertEquals("", template.apply(params));
	}
	
	@DisplayName("Try to compile without locale")
	@Test
	void testNoLocale() throws IOException {
		template = hbs.compileInline(String.format(HELPER_PATTERN, "test"));
		assertEquals("", template.apply(null));
	}
	
	@DisplayName("Test path formatting")
	@Test
	void testPathFormatting() throws IOException {
		String output = renderer.render(parser.parse("# Test"));
		String[] names = new String[] { "test.md", "/test", "test.txt", "/test.txt" };
		for(String name : names) {
			template = hbs.compileInline(String.format(HELPER_PATTERN, name));
			assertEquals(output, template.apply(params));	
		}
	}
	
	@DisplayName("Test locale setting")
	@Test
	void testLocale() throws IOException {
		String outputEn = renderer.render(parser.parse("# Test"));
		String outputRu = renderer.render(parser.parse("# Тест"));
		template = hbs.compileInline(String.format(HELPER_PATTERN, "test"));
		params.put("locale", "en");
		assertEquals(outputEn, template.apply(params));	
		params.put("locale", "ru");
		assertEquals(outputRu, template.apply(params));	
	}
	
	@DisplayName("Compile Handlebars inside Markdown")
	@Test
	void testHandlebarsInMarkdown() throws IOException {
		String output = renderer.render(parser.parse("# Test"));
		String text = "{{md \"testhbs.md\" }}";
		params.put("text", "Test");
		assertEquals(output, hbs.compileInline(text).apply(params));
	}
}
