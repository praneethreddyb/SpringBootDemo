package com.demo.core.exception;

import javax.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import com.demo.core.response.entity.ResponseEntity;
import com.demo.core.util.ZcMap;

@SuppressWarnings("serial")
@org.springframework.web.bind.annotation.RestControllerAdvice
public class RestControllerAdvice {

	@ExceptionHandler(value = MessageException.class)
	public ResponseEntity messageException(HttpServletRequest request, MessageException exception) {
		return new ResponseEntity(request,new ZcMap(){{
			put("success", false);
			put("message", exception.getMessage());			
		}},HttpStatus.OK);
	}
}
