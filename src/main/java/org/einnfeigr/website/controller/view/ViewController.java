package org.einnfeigr.website.controller.view;

import javax.servlet.http.HttpServletRequest;

import org.einnfeigr.website.ControllerUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;


@RestController
public class ViewController {
	
	@GetMapping(value={"/", "/info", "/contacts", "/about", "/fridrum", "/faq"})
	ModelAndView main(HttpServletRequest request, ControllerUtils builder) throws Exception {
		String name = request.getRequestURI();
		name = name.length() == 1 ? "main" : name.replace("/", "");
		return builder.buildMav(name);
	}	

}
