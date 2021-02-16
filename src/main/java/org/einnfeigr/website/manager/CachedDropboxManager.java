package org.einnfeigr.website.manager;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.einnfeigr.website.pojo.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.files.ListFolderErrorException;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class CachedDropboxManager extends DropboxManager implements CachedCmsManager {

	private LoadingCache<String, byte[]> cache;
	private LoadingCache<String, File> folderCache;	
	private final static Logger log = LoggerFactory.getLogger(CachedDropboxManager.class);
	
	public CachedDropboxManager() throws ListFolderErrorException, DbxException {
		super();
		cache = CacheBuilder.newBuilder()
				.maximumSize(60)
				.expireAfterAccess(45, TimeUnit.MINUTES)
				.build(new CacheLoader<>() {
					public byte[] load(String key) throws Exception {
						return superReadFile(key);
					}
				});
		folderCache = CacheBuilder.newBuilder()
				.maximumSize(150)
				.expireAfterAccess(45, TimeUnit.MINUTES)
				.build(new CacheLoader<>() {
					public File load(String key) throws Exception {
						return superReadFolder(key);
					}
				});
	}

	@Override
	public byte[] readFile(String path) throws IOException {
		try {
			return cache.get(path);
		} catch (ExecutionException e) {
			log.info("Failed to get file content from cache, nested exception is: ", e);
			return super.readFile(path);
		}
	}
	
	@Override
	public File readFolder(String path) throws IOException {
		try {
			return folderCache.get(path);
		} catch(ExecutionException e) {
			log.info("Failed to get folder content from cache, nested exception is: ", e);
			return super.readFolder(path);
		}
	}
	
	@Override
	public void writeFile(String path, byte[] bytes) throws IOException {
		cache.put(path, bytes);
		super.writeFile(path, bytes);
	}
	
	@Override
	public void delete(String path) throws IOException {
		cache.invalidate(path);
		folderCache.invalidate(path);
		super.delete(path);
	}
	
	private byte[] superReadFile(String key) throws IOException {
		return super.readFile(key);
	}
	
	private File superReadFolder(String key) throws IOException {
		return super.readFolder(key);
	}
	
	public void resetCache() {
		cache.invalidateAll();
		folderCache.invalidateAll();
	}
	
}
