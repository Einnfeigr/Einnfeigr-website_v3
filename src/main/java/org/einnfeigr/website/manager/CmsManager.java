package org.einnfeigr.website.manager;

import java.io.IOException;

import org.einnfeigr.website.pojo.File;

public interface CmsManager {

	byte[] readFile(String path) throws IOException;
	File readFolder(String path) throws IOException;
	
	void writeFile(String path, byte[] content) throws IOException;
	void createFolder(String path) throws IOException;
	
	void delete(String path) throws IOException;
	
}
