package org.einnfeigr.website.controller;

import org.einnfeigr.website.manager.CachedCmsManager;
import org.einnfeigr.website.manager.CmsManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiController {

	@Autowired
	private CmsManager cmsManager;
	
	@PostMapping("/api/cache/reset")
	ResponseEntity<String> resetCache() {
		if(!(cmsManager instanceof CachedCmsManager)) {
			return new ResponseEntity<>("caching is not supported by cms manager", 
					HttpStatus.BAD_REQUEST);
		}
		((CachedCmsManager)cmsManager).resetCache();
		return new ResponseEntity<>("ok", HttpStatus.OK);
	}
	
}
