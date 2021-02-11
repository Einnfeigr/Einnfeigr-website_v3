package org.einnfeigr.website;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.FolderMetadata;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;

@Component
public class AlbumController {

	@Autowired
	private DropboxManager dropboxManager;
	
	public final static String LOCALIZED_NAMES_FILE = "names.properties";
	private final static Logger log = LoggerFactory.getLogger(DropboxManager.class);
	
	public Album parseAlbum(Locale locale, String path) throws DbxException, FileNotFoundException {
		Album album = new Album(path);
		fillInAlbum(locale, album, dropboxManager.readFolder(path));
		for(Album child : album.getAlbums()) {
			fillInAlbum(locale, child, dropboxManager.readFolder(child.getDropboxPath()));
		}
		return album;
	}
	
	//TODO refactor
	private void fillInAlbum(Locale locale, Album album, List<ListFolderResult> results) {
		for(ListFolderResult result : results) {
			for(Metadata metadata : result.getEntries()) {
				String path = metadata.getPathDisplay();
				if(metadata instanceof FileMetadata) {
					if(isImage(metadata.getName())) {
						album.addImage(new Image(path, metadata.getName()));
						continue;
					} 
					if(metadata.getName().contains(LOCALIZED_NAMES_FILE)) {
						album.setLocalizedNames(parseLocalizedNames(path));
						album.setLocale(locale);
					}
				} else if(metadata instanceof FolderMetadata) {
					album.addAlbum(new Album(path, metadata.getName()));
				}
			}
		}
	}
	
	private Properties parseLocalizedNames(String path) {
		Properties props = new Properties();
		try {
			props.load(new ByteArrayInputStream(dropboxManager.readFileContent(path)));
		} catch (DbxException | IOException e) {
			log.warn("Unable to read localized names file '"+path+"'\nNested exception is:", e);
		}
		return props;
	}
	
	public static boolean isImage(String name) {
		name = name.toLowerCase();
		return name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png")
				|| name.endsWith(".webp") || name.endsWith(".gif");
	}

}
