package org.einnfeigr.website.pojo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Album {
	
	private String path;
	private String name;
	private String localizedName;
	
	private Map<String, String > localizedNames = new HashMap<>();
	private List<Album> albums = new ArrayList<>();
	private List<Image> images = new ArrayList<>();

	private Locale locale;
	private Image preview;
	
	public final static String PATH_HOME = "/fridrum/albums";
	
	public Album(String path) {
		this.path = path;
		this.name = parseName(path);
		localizedName = name;
	}
	
	public void setLocale(Locale locale) {
		this.locale = locale;
		localizedName = localizedNames.getOrDefault(locale.getLanguage(), name);
	}
	
	public String getLocalizedName() {
		return localizedName;
	}
	
	private String parseName(String path) {
		return path.substring(path.lastIndexOf("/")+1);
	}
	
	public void addImage(Image image) {
		images.add(image);
	}
	
	public void addAlbum(Album album) {
		albums.add(album);
	}
	
	public List<Album> getAlbums() {
		return albums;
	}

	public List<Image> getImages() {
		return images;
	}

	public String getPath() {
		return path;
	}

	public String getDropboxPath() {
		return path.replace(PATH_HOME, "");
	}

	public void setPath(String path) {
		this.path = path;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<String, String> getLocalizedNames() {
		return localizedNames;
	}

	public void setLocalizedNames(Map<String, String> localizedNames) {
		this.localizedNames = localizedNames;
		if(locale != null) {
			localizedName = localizedNames.getOrDefault(locale.getLanguage(), name);
		}
	}

	public Locale getLocale() {
		return locale;
	}

	public Image getPreview() {
		return preview;
	}

	public void setPreview(Image preview) {
		this.preview = preview;
	}
	
}
