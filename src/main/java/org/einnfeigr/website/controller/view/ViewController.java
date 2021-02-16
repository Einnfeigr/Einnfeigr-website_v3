package org.einnfeigr.website.controller.view;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.einnfeigr.website.ControllerUtils;
import org.einnfeigr.website.manager.AlbumManager;
import org.einnfeigr.website.manager.NoteManager;
import org.einnfeigr.website.pojo.Album;
import org.einnfeigr.website.pojo.Folder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;


@RestController
public class ViewController {
	
	@Autowired
	private AlbumManager albumController;
	
	@Autowired
	private NoteManager noteController;
	
	@GetMapping(value={"/", "/info", "/about", "/faq", "/fridrum"})
	ModelAndView handle(HttpServletRequest request, ControllerUtils builder) throws Exception {
		String name = request.getRequestURI();
		name = name.equals("/") ? "main" : name.replace("/", "");
		return builder.buildMav(name);
	}	

	@GetMapping(value={"/fridrum/albums", "/fridrum/albums/{path}"})
	ModelAndView albums(@PathVariable(required=false) String path, Locale locale,
			ControllerUtils builder) throws Exception {
		String albumPath = path == null ? "/albums" : "/albums/"+path;  
		Album album = albumController.parseAlbum(locale, albumPath);
		return builder.buildMav("album", 
				"album", album,
				"albums", album.getAlbums(),
				"images", album.getImages());
	}

	@GetMapping("/fridrum/notes/**")
	ModelAndView notes(HttpServletRequest request, ControllerUtils builder, Locale locale)
			throws Exception {
		String path = request.getRequestURI().replace("/fridrum/notes", "");
		path = path.startsWith("/") ? path.substring(1) : path;
		if(path.equals("") ) {
			Folder folder = noteController.getAll(locale);
			return builder.buildMav("notes", 
					"folders", folder.getFolders(), 
					"notes", folder.getNotes());
		}
		String text = noteController.readNote(path, locale);
		return builder.buildMav("note", "text", text);
	}
	
}
