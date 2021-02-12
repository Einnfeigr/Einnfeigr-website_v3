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
		album.setLocale(locale);
		fillInAlbum(album, dropboxManager.readFolder(path));
		for(Album child : album.getAlbums()) {
			fillInAlbum(child, dropboxManager.readFolder(child.getDropboxPath()));
		}
		return album;
	}
	
	private void fillInAlbum(Album album, List<ListFolderResult> results) {
		for(ListFolderResult result : results) {
			for(Metadata metadata : result.getEntries()) {
				if(metadata instanceof FileMetadata) {
					addFile(metadata, album);
				} else if(metadata instanceof FolderMetadata) {
					Album child = new Album(metadata.getPathDisplay(), metadata.getName());
					child.setLocale(album.getLocale());
					album.addAlbum(child);
				}
			}
		}
	}
	
	private void addFile(Metadata metadata, Album album) {
		String path = metadata.getPathDisplay();
		String name = metadata.getName();
		if(isImage(metadata.getName())) {
			album.addImage(new Image(path, name));
		} else if(name.contains(LOCALIZED_NAMES_FILE)) {
			album.setLocalizedNames(parseLocalizedNames(path));
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
