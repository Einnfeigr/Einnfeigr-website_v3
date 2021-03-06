package org.einnfeigr.website.pojo;

public class Image {

	private String link;
	private String name;
	
	public final static String LINK_HOME = "/fridrum/media";
	
	public Image(String link, String name) {
		this.link = LINK_HOME+link;
		this.name = name;
	}

	public Image(File file) {
		this.link = LINK_HOME+file.getPath();
		this.name = file.getName();
	}

	public String getLink() {
		return link;
	}
	
	public String getDropboxLink() {
		return link.replace(LINK_HOME, "");
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
