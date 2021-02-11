package org.einnfeigr.website;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.servlet.ServletException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


@ControllerAdvice
public class ErrorController extends ResponseEntityExceptionHandler {
	
	private final static Logger log = LoggerFactory.getLogger(ErrorController.class);
	
	@ExceptionHandler(Exception.class)
	public ModelAndView handle(Exception e, ControllerUtils builder)
			throws IOException, ServletException {
		log.info("Handler resolved exception", e);
		String path = "err";
		boolean error404;
		HttpStatus responseStatus;
		if(e instanceof FileNotFoundException || e instanceof NoHandlerFoundException) {
			responseStatus = HttpStatus.NOT_FOUND;
			error404 = true;
		} else {
			responseStatus = HttpStatus.INTERNAL_SERVER_ERROR;
			error404 = false;
		}
		return builder.buildMav(path, responseStatus, "error.404", error404);
	}

	
}
