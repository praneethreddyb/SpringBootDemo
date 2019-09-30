package com.demo.core.response.entity;

import java.sql.Timestamp;
import javax.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import com.demo.core.util.Util;
import com.demo.core.util.ZcMap;

public class ResponseEntity extends org.springframework.http.ResponseEntity<ZcMap>{
	
//	private static final Logger log = LogManager.getLogger(ResponseEntity.class);

	public ResponseEntity(HttpStatus status) {
		super(status);
	}
	
	public ResponseEntity(HttpServletRequest request, ZcMap body) {
		this(request,body,HttpStatus.OK);
	}
	public ResponseEntity(HttpServletRequest request) {
		this(request,new ZcMap(),HttpStatus.OK);
	}
	
	public ResponseEntity(HttpServletRequest request, ZcMap body, HttpStatus status) {
		super(body, status);
		if(body.get("success")==null)body.put("success", true);
		body.putAll(body);
		body.put("serverDateTime", Util.getDateTimeString(new Timestamp(new java.util.Date().getTime())));
	}
}
