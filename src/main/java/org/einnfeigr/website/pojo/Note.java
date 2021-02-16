package org.einnfeigr.website.pojo;

import java.util.List;
import java.util.Locale;

import org.einnfeigr.website.manager.NoteManager;

public class Note {

	private String path;
	private String name;
	private List<Locale> locales;
	
	public Note(File file) {
		this.path = file.getName().replace(NoteManager.NOTES_EXTENSION, "");
		this.name = file.getName();
	}
	
	public String getPath() {
		return path;
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

	public List<Locale> getLocales() {
		return locales;
	}

	public void setLocales(List<Locale> locales) {
		this.locales = locales;
	}
	
	
}
