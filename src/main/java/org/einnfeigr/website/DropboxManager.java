package org.einnfeigr.website;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.springframework.stereotype.Component;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.DownloadErrorException;

@Component
public class DropboxManager {

	private DbxClientV2 client;

	public DropboxManager() {
		DbxRequestConfig config = DbxRequestConfig.newBuilder("einnfeigr website").build();
		client = new DbxClientV2(config, System.getenv("dropbox.access_token"));
	}
	
	public byte[] readFileContent(String path) 
			throws DownloadErrorException, DbxException, IOException {
		try(InputStream is = client.files().download(formatPath(path)).getInputStream()) {
			return is.readAllBytes();
		} catch(DownloadErrorException e) {
			if(e.errorValue.isPath()) {
				throw new FileNotFoundException();
			} else {
				throw e;
			}
		}
	}
	
	public void writeFileContent(String path, byte[] bytes) throws DbxException, IOException {
		try(OutputStream os =  client.files().upload(formatPath(path)).getOutputStream()) {
			os.write(bytes);
		}
	}
	
	private String formatPath(String path) {
		if(!path.startsWith("/")) {
			path = "/"+path;
		}
		return path;
	}
	
}
