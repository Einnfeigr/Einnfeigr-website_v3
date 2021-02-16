package org.einnfeigr.website.manager;

import java.io.IOException;
import java.util.Locale;

import org.einnfeigr.website.pojo.File;
import org.einnfeigr.website.pojo.Folder;
import org.einnfeigr.website.pojo.Note;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;

@Component
public class NoteManager {

	@Autowired
	private CmsManager cmsManager;
	
	private final static String NOTES_PATH = "/notes/%s/%s";
	public final static String NOTES_EXTENSION = ".md";
	
	private static final HtmlRenderer renderer = HtmlRenderer.builder().build();
	private static final Parser parser = Parser.builder().build();
	
	public Folder getAll(Locale locale) throws IOException {
		Folder folder = new Folder();
		String path = String.format(NOTES_PATH, locale.getLanguage(), "");
		folder.setPath(path);
		folder = parseFolder(folder, cmsManager.readFolder(path));
		return folder;
	}
	
	private Folder parseFolder(Folder folder, File file) throws IOException {
		for(File child : file.getChildren()) {
			if(!child.isFolder()) {
				String name = child.getName();
				if(name.endsWith(NOTES_EXTENSION)) {
					folder.addNote(new Note(child));
				} 
			} else {
				Folder childFolder = new Folder(child);
				folder.addFolder(parseFolder(childFolder, cmsManager.readFolder(child.getPath())));
			}
		}
		return folder;
	}
	
	public String readNote(String name, Locale locale) throws IOException {
		name = name.endsWith(".md") ? name : name+".md";
		String path = String.format(NOTES_PATH, locale.getLanguage(), name);
		String content;
		content = new String(cmsManager.readFile(path));
		return renderer.render(parser.parse(content));
	}
	
}
