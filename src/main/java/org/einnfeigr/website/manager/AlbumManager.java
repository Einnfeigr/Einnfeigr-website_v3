package org.einnfeigr.website.manager;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.einnfeigr.website.pojo.Album;
import org.einnfeigr.website.pojo.File;
import org.einnfeigr.website.pojo.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class AlbumManager {

	@Autowired
	private CmsManager cmsManager;
	
	public final static String LOCALES_FILE = "locales.properties";
	private final static String LOCALES_ERROR = "Unable to load localized names file for album %s, "
			+ "nested exception is: ";
	private final static Logger log = LoggerFactory.getLogger(AlbumManager.class);
	
	public Album parseAlbum(Locale locale, String path) throws IOException {
		Album album = new Album(path);
		album.setLocale(locale);
		File file = cmsManager.readFolder(path);
		for(File child : file.getChildren()) {
			if(!child.isFolder() && child.getName().equals(LOCALES_FILE)) {
				album.setLocalizedNames(parseLocalesFile(child));
			} else if(isImage(child.getName())) {
				album.addImage(new Image(child));
			}
		}
		return album;
	}
	
	private Map<String, String> parseLocalesFile(File file) {
		Map<String, String> data = new HashMap<>();
		try {
			String content = new String(cmsManager.readFile(file.getPath()));
			ObjectMapper mapper = new ObjectMapper();
			data = mapper.readValue(content, new TypeReference<Map<String, String>>(){});
		} catch(IOException e) {
			log.warn(String.format(LOCALES_ERROR, file.getPath()), e);
		}
		return data;
	}
	
	public static boolean isImage(String name) {
		name = name.toLowerCase();
		return name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png")
				|| name.endsWith(".webp") || name.endsWith(".gif");
	}
	
}
