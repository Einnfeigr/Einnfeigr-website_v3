package org.einnfeigr.website.pojo;

import java.util.ArrayList;
import java.util.List;

public class File {

	private String path;
	private boolean isFolder;
	
	private List<File> children = new ArrayList<>();
	private File parent;
	
	public String getName() {
		return path.substring(path.lastIndexOf("/")+1);
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public List<File> getChildren() {
		return children;
	}
	public void setChildren(List<File> children) {
		this.children = children;
	}
	public void addChild(File file) {
		children.add(file);
	}
	public File getParent() {
		return parent;
	}
	public void setParent(File parent) {
		this.parent = parent;
	}
	public boolean isFolder() {
		return isFolder;
	}
	public void setFolder(boolean isFolder) {
		this.isFolder = isFolder;
	}
	
}
