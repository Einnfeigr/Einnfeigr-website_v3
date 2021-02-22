package org.einnfeigr.website.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.data.MutableDataSet;

public class FileMarkdownHelper implements Helper<Object> {

	private final static Logger log = LoggerFactory.getLogger(FileMarkdownHelper.class);
	private final static String TEXT_PATH = "/text/%s/";
	private final static String TEXT_EXTENSION = ".md";
	private final static DataHolder OPTIONS = new MutableDataSet()
			.set(Parser.EXTENSIONS, Arrays.asList(TablesExtension.create()));
	private final static Parser parser = Parser.builder(OPTIONS).build();
	private final static HtmlRenderer renderer = HtmlRenderer.builder(OPTIONS).build();
	private final static Handlebars handlebars = new Handlebars();
	
	@Override
	public Object apply(Object context, Options options) throws IOException {
		String filename = formatFilename(context.toString());
		if(options.get("locale") == null) {
			return null;
		}
		String path = String.format(TEXT_PATH+filename, options.get("locale").toString());
		String content = readFile(path);
	    content = renderer.render(parser.parse(content));
	    if(content.contains("{{") && content.contains("}}")) {
	    	content = handlebars.compileInline(content).apply(options.context.get("root"));
	    }
	    return new Handlebars.SafeString(content);
	}
	
	private String formatFilename(String name) {
		name = name.startsWith("/") ? name.substring(1) : name;
		name = name.contains(".") ? name.substring(0, name.indexOf(".")) : name;
		return name + TEXT_EXTENSION;
	}
	
	private String readFile(String path) {
		StringBuilder content = new StringBuilder();
		InputStream is = this.getClass().getResourceAsStream(path);
		try(BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
			reader.lines().forEach(l -> content.append(l+"\n"));
		} catch (IOException | NullPointerException e) {
			log.warn("Unable to load markdown file \'"+path+"\', nested exception is: ", e);
			return "";
		}
		return content.toString();
	}
	
}
