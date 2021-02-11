package org.einnfeigr.website.controller.view;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.einnfeigr.website.Album;
import org.einnfeigr.website.AlbumController;
import org.einnfeigr.website.ControllerUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;


@RestController
public class ViewController {
	
	@Autowired
	private AlbumController albumController;
	
	@GetMapping(value={"/", "/info", "/about", "/faq", "/fridrum"})
	ModelAndView handle(HttpServletRequest request, ControllerUtils builder) throws Exception {
		String name = request.getRequestURI();
		name = name.equals("/") ? "main" : name.replace("/", "");
		return builder.buildMav(name);
	}	

	@GetMapping(value={"/fridrum/albums", "/fridrum/albums/{path}"})
	ModelAndView fridrum(@PathVariable(required=false) String path, Locale locale,
			ControllerUtils builder) throws Exception {
		String albumPath = path == null ? "/albums" : "/albums/"+path;  
		Album album = albumController.parseAlbum(locale, albumPath);
		return builder.buildMav("album", "albums", album.getAlbums(), "images", album.getImages());
	}
	
	@GetMapping(value= {"/fridrum/notes", "/fridrum/notes/{path}"})
	ModelAndView notes(@PathVariable(required=false) String path, ControllerUtils builder)
		throws Exception {
		return builder.buildMav("notes");
	}
}
