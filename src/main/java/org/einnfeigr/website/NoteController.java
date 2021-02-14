package org.einnfeigr.website;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import org.einnfeigr.website.pojo.Folder;
import org.pegdown.PegDownProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.files.DownloadErrorException;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;

@Component
public class NoteController {

	@Autowired
	private DropboxManager dropboxManager;
	
	private final static String NOTES_PATH = "/notes/%s/%s";
	
	public String readNote(String name, Locale locale)
			throws DownloadErrorException, DbxException, IOException {
		name = name.endsWith(".md") ? name : name+".md";
		String path = String.format(NOTES_PATH, locale.getLanguage(), name);
		String content = new String(dropboxManager.readFileContent(path));
		PegDownProcessor processor = new PegDownProcessor();
		return processor.markdownToHtml(content);
	}
	
	public Folder listContent(String path, Locale locale) 
			throws FileNotFoundException, DbxException {
		path = String.format(NOTES_PATH, locale.getLanguage(), path);
		List<ListFolderResult> results = dropboxManager.readFolder(path);
		Folder folder = new Folder();
		for(ListFolderResult result : results) {
			for(Metadata meta : result.getEntries()) {
				if(meta instanceof FileMetadata) {
					folder.getFiles().add(meta.getPathDisplay());
				} else {
					Folder child = new Folder();
					child.setPath(meta.getPathDisplay());
					folder.getFolders().add(child);
				}
			}
		}
		return folder;
	}
	
	
}
