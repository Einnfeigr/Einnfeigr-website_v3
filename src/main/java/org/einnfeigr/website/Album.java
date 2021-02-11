package org.einnfeigr.website;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

public class Album {
	
	private String path;
	private String name;
	private String localizedName;
	
	public String getLocalizedName() {
		return localizedName;
	}
	
	private Properties localizedNames = new Properties();
	private List<Album> albums = new ArrayList<>();
	private List<Image> images = new ArrayList<>();
	
	public final static String PATH_HOME = "/fridrum/albums";
	
	public Album(String path, String name) {
		this.path = PATH_HOME+path;
		this.name = name;
		localizedName = name;
	}
	
	public Album(String path) {
		this.path = path;
		this.name = parseName(path);
		localizedName = name;
	}
	
	public void setLocale(Locale locale) {
		localizedName = localizedNames.getOrDefault(locale.getLanguage(), name).toString();
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

	public Properties getLocalizedNames() {
		return localizedNames;
	}

	public void setLocalizedNames(Properties localizedNames) {
		this.localizedNames = localizedNames;
	}
	
}
