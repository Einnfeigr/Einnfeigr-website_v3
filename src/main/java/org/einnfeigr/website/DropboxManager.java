package org.einnfeigr.website;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.DownloadErrorException;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;

@Component
public class DropboxManager {

	private DbxClientV2 client;

	public DropboxManager() {
		DbxRequestConfig config = DbxRequestConfig.newBuilder("einnfeigr website").build();
		client = new DbxClientV2(config, System.getenv("DROPBOX_ACCESS_TOKEN"));
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
	
	public List<Metadata> readFolder(String path) throws DbxException {
		try {
			List<ListFolderResult> results = new ArrayList<>();
			ListFolderResult result = client.files().listFolder(path);
			results.add(result);
			while(result.getHasMore()) {
				result = client.files().listFolderContinue(result.getCursor());
				results.add(result);
			} 
			List<Metadata> metadata = new ArrayList<>();
			results.forEach((r) -> {
				metadata.addAll(r.getEntries());
			});
			return metadata;
		} catch (DbxException e) {
			throw e;
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
