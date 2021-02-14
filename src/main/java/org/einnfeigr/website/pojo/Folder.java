package org.einnfeigr.website.pojo;

import java.util.ArrayList;
import java.util.List;

public class Folder {

	private List<String> files = new ArrayList<>();
	private List<Folder> folders = new ArrayList<>();
	private String path;
	
	public List<String> getFiles() {
		return files;
	}
	public void setFiles(List<String> files) {
		this.files = files;
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
	
}
