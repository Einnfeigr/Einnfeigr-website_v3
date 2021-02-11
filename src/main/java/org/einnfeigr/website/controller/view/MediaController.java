package org.einnfeigr.website.controller.view;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLConnection;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.einnfeigr.website.DropboxManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.dropbox.core.DbxException;

@RestController
public class MediaController {
	
	@Autowired
	private DropboxManager dropboxManager;
	
	public final static int CACHE_UPDATE_RATE = 6000;
	public static long CACHE_AGE_SECONDS = 1800;
	
	public final static String MAPPING_PATH = "/fridrum/media/";
	
	private Map<String, byte[]> mediaCache = new HashMap<>();
	private Map<String, LocalDateTime> cacheExpiration = new HashMap<>();
	
	//TODO refactor
	@GetMapping(MAPPING_PATH+"**")
	@ResponseBody
	ResponseEntity<byte[]> getMedia(HttpServletRequest request, HttpServletResponse response) 
			throws FileNotFoundException, DbxException, IOException {
		String path = request.getRequestURI().replace(MAPPING_PATH, "");
		byte[] body = mediaCache.containsKey(path) 
				? mediaCache.get(path) : dropboxManager.readFileContent(path); 
		String mediaType = URLConnection.getFileNameMap().getContentTypeFor(path);
		CacheControl cacheControl = CacheControl.empty();
		cacheControl.cachePublic();
		cacheControl.sMaxAge(Duration.ofHours(1));
		HttpHeaders headers = new HttpHeaders();
		headers.setCacheControl(cacheControl);
		headers.setContentType(MediaType.parseMediaType(mediaType));
		mediaCache.put(path, body);
		cacheExpiration.put(path, LocalDateTime.now().plusSeconds(CACHE_AGE_SECONDS));
		return new ResponseEntity<>(body, headers, HttpStatus.OK);
	}	
	
	@Scheduled(fixedRate=CACHE_UPDATE_RATE)
	void clearCache() {
		LocalDateTime timeNow = LocalDateTime.now();
		List<String> removeList = new ArrayList<>();
		for(Entry<String, LocalDateTime> entry : cacheExpiration.entrySet()) {
			if(entry.getValue().isBefore(timeNow)) {
				removeList.add(entry.getKey());
			}
		}
		for(String key : removeList) {
			mediaCache.remove(key);
			cacheExpiration.remove(key);
		}
	}
}
