package org.einnfeigr.website.manager;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.einnfeigr.website.pojo.File;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.DeleteErrorException;
import com.dropbox.core.v2.files.DownloadErrorException;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.ListFolderContinueErrorException;
import com.dropbox.core.v2.files.ListFolderErrorException;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.files.UploadUploader;

public class DropboxManager implements CmsManager {

	private DbxClientV2 client;

	public final static String NOTES_FOLDER = "/notes";
	public final static String ALBUMS_FOLDER = "/albums";
	
	public DropboxManager() throws ListFolderErrorException, DbxException {
		DbxRequestConfig config = DbxRequestConfig.newBuilder("einnfeigr website").build();
		client = new DbxClientV2(config, System.getenv("DROPBOX_ACCESS_TOKEN"));
	}
	
	public byte[] readFile(String path) throws IOException {
		try(InputStream is = client.files().download(formatPath(path)).getInputStream()) {
			return is.readAllBytes();
		} catch(DownloadErrorException e) {
			if(e.errorValue.isPath()) {
				throw new FileNotFoundException();
			} else {
				throw new IOException(e);
			}
		} catch(DbxException e) {
			throw new IOException(e);
		}
	}
	
	public File readFolder(String path) throws IOException {
		try {
			File file = new File();
			file.setFolder(true);
			file.setPath(path);
			for(ListFolderResult result : listFolder(path)) {
				parseFiles(result, file);
			}
			return file;
		} catch (ListFolderErrorException e) {
			if(e.errorValue.isPath()) {
				throw new FileNotFoundException(e.getMessage());
			}
			throw new IOException(e);
		} catch (DbxException e) {
			throw new IOException(e);
		}
	}
	
	private void parseFiles(ListFolderResult result, File file) throws IOException {
		for(Metadata metadata : result.getEntries()) {
			if(metadata instanceof FileMetadata) {
				File child = new File();
				child.setFolder(false);
				child.setPath(metadata.getPathDisplay());
				file.addChild(child);
			} else {
				file.addChild(readFolder(metadata.getPathDisplay()));
			}
		}
	}
	
	private List<ListFolderResult> listFolder(String path) 
			throws ListFolderContinueErrorException, DbxException {
		List<ListFolderResult> results = new ArrayList<>();
		ListFolderResult result = client.files().listFolder(path);
		results.add(result);
		while(result.getHasMore()) {
			result = client.files().listFolderContinue(result.getCursor());
			results.add(result);
		} 
		return results;
	}
	
	public void writeFile(String path, byte[] bytes) throws IOException {
		UploadUploader loader;
		try {
			loader = client.files().upload(formatPath(path));
			loader.uploadAndFinish(new ByteArrayInputStream(bytes));
		} catch (DbxException e) {
			throw new IOException(e);
		}
	}
	
	public void createFolder(String path) throws IOException {
		try {
			client.files().createFolderV2(formatPath(path));
		} catch (DbxException e) {
			throw new IOException(e);
		}
	}
	
	public void delete(String path) throws IOException {
		try {
			client.files().deleteV2(formatPath(path));
		} catch (DeleteErrorException e) {
			if(e.errorValue.isPathLookup()) {
				throw new FileNotFoundException(e.getMessage());
			} else {
				throw new IOException(e);
			}
		} catch (DbxException e) {
			throw new IOException(e);
		}
	}
	
	private String formatPath(String path) {
		if(!path.startsWith("/")) {
			path = "/"+path;
		}
		return path;
	}
	
}
