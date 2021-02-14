package org.einnfeigr.website;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.CreateFolderErrorException;
import com.dropbox.core.v2.files.DeleteErrorException;
import com.dropbox.core.v2.files.DownloadErrorException;
import com.dropbox.core.v2.files.ListFolderErrorException;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.UploadUploader;

@Component
public class DropboxManager {

	private DbxClientV2 client;

	public final static String NOTES_FOLDER = "/notes";
	public final static String ALBUMS_FOLDER = "/albums";
		
	public DropboxManager() throws ListFolderErrorException, DbxException {
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
	
	public List<ListFolderResult> readFolder(String path) 
			throws DbxException, FileNotFoundException {
		try {
			List<ListFolderResult> results = new ArrayList<>();
			ListFolderResult result = client.files().listFolder(path);
			results.add(result);
			while(result.getHasMore()) {
				result = client.files().listFolderContinue(result.getCursor());
				results.add(result);
			} 
			return results;
		} catch (ListFolderErrorException e) {
			if(e.errorValue.isPath()) {
				throw new FileNotFoundException(e.getMessage());
			}
			throw e;
		} catch (DbxException e) {
			throw e;
		}
	}
	
	public void writeFileContent(String path, byte[] bytes) throws DbxException, IOException {
		UploadUploader loader = client.files().upload(formatPath(path));
		loader.uploadAndFinish(new ByteArrayInputStream(bytes));
	}
	
	public void createFolder(String path) throws CreateFolderErrorException, DbxException {
		client.files().createFolderV2(formatPath(path));
	}
	
	public void delete(String path) throws DeleteErrorException, DbxException {;
		client.files().deleteV2(formatPath(path));
	}
	
	private String formatPath(String path) {
		if(!path.startsWith("/")) {
			path = "/"+path;
		}
		return path;
	}
	
}
