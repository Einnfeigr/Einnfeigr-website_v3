package org.einnfeigr.website.controller;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.servlet.ServletException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


@ControllerAdvice
public class ErrorController extends ResponseEntityExceptionHandler {
	
	private final static Logger log = LoggerFactory.getLogger(ErrorController.class);
	
	@ExceptionHandler(Throwable.class)
	public ResponseEntity<Object> handle(Exception e, WebRequest request)
			throws IOException, ServletException {
		log.info("Handler resolved exception", e);
		String path;
		HttpStatus responseStatus;
		if(e instanceof FileNotFoundException || e instanceof NoHandlerFoundException) {
			path = "error/404";
			responseStatus = HttpStatus.NOT_FOUND;
		} else {
			path = "error/5xx";
			responseStatus = HttpStatus.INTERNAL_SERVER_ERROR;
		}
		return new ResponseEntity<>(path, responseStatus);
	}

	
}
