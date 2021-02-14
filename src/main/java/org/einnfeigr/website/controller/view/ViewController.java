package org.einnfeigr.website.controller.view;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.einnfeigr.website.AlbumController;
import org.einnfeigr.website.ControllerUtils;
import org.einnfeigr.website.NoteController;
import org.einnfeigr.website.pojo.Album;
import org.einnfeigr.website.pojo.Folder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.dropbox.core.DbxException;


@RestController
public class ViewController {
	
	@Autowired
	private AlbumController albumController;
	
	@Autowired
	private NoteController noteController;
	
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
	
	@GetMapping("/fridrum/notes")
	ModelAndView notes(ControllerUtils builder, Locale locale) 
			throws FileNotFoundException, DbxException {
		Folder folder = noteController.listContent("/", locale);
		return builder.buildMav("notes", 
				"folders", folder.getFolders(), 
				"notes", folder.getFiles());
	}

	@GetMapping("/fridrum/notes/{path}")
	ModelAndView note(@PathVariable String path, ControllerUtils builder, Locale locale)
			throws Exception {
		String text = noteController.readNote(path, locale);
		return builder.buildMav("note", "text", text);
	}
	
}
