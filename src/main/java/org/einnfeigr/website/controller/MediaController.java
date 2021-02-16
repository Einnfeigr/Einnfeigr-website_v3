package org.einnfeigr.website.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLConnection;
import java.time.Duration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.einnfeigr.website.manager.CmsManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.dropbox.core.DbxException;

@RestController
public class MediaController {
	
	@Autowired
	private CmsManager cmsManager;
	
	public final static String MAPPING_PATH = "/fridrum/media/";
	private final static Logger log = LoggerFactory.getLogger(MediaController.class);
	
	@GetMapping(MAPPING_PATH+"**")
	@ResponseBody
	ResponseEntity<byte[]> getMedia(HttpServletRequest request, HttpServletResponse response) 
			throws FileNotFoundException, DbxException, IOException {
		String path = request.getRequestURI().replace(MAPPING_PATH, "");
		String mediaType = URLConnection.getFileNameMap().getContentTypeFor(path);
		CacheControl cacheControl = CacheControl.empty();
		cacheControl.cachePublic();
		cacheControl.sMaxAge(Duration.ofHours(1));
		HttpHeaders headers = new HttpHeaders();
		headers.setCacheControl(cacheControl);
		headers.setContentType(MediaType.parseMediaType(mediaType));
		return new ResponseEntity<>(cmsManager.readFile(path), headers, HttpStatus.OK);
	}	
	
}
