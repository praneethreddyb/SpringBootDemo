package com.demo.core.base.controller;

import javax.servlet.http.HttpServletRequest;
import com.demo.core.bo.SessionInfo;

public class BaseController {

	public SessionInfo sessionInfo;
	
	public BaseController(HttpServletRequest request) {
		sessionInfo=new SessionInfo();
	}
	
}
