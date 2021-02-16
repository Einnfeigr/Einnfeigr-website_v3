package org.einnfeigr.website.pojo;

import java.util.ArrayList;
import java.util.List;

public class Folder {

	private List<Note> notes = new ArrayList<>();
	private List<Folder> folders = new ArrayList<>();
	private String path;
	private String name;
	
	public Folder() {}
	
	public Folder(File file) {
		this.path = file.getPath();
		this.name = path.substring(path.lastIndexOf("/")+1);
	}
	
	public List<Folder> getFolders() {
		return folders;
	}
	public void setFolders(List<Folder> folders) {
		this.folders = folders;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public List<Note> getNotes() {
		return notes;
	}
	public void setNotes(List<Note> notes) {
		this.notes = notes;
	}
	public void addNote(Note note) {
		notes.add(note);
	}
	public void addFolder(Folder folder) {
		folders.add(folder);
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
