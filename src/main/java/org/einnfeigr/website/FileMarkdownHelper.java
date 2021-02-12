package org.einnfeigr.website;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.pegdown.PegDownProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;

public class FileMarkdownHelper implements Helper<Object> {

	private final static Logger log = LoggerFactory.getLogger(FileMarkdownHelper.class);
	private final static String TEXT_PATH = "/text/%s/";
	private final static String TEXT_EXTENSION = ".md";
	
	@Override
	public Object apply(Object context, Options options) {
		String filename = formatFilename(context.toString());
		if(options.get("locale") == null) {
			return null;
		}
		String path = String.format(TEXT_PATH+filename, options.get("locale").toString());
		String content = readFile(path);
	    PegDownProcessor processor = new PegDownProcessor();
	    return new Handlebars.SafeString(processor.markdownToHtml(content));
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
