package com.demo.core.bo;

import java.util.Date;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import com.demo.core.util.Util;


public class SessionInfo {

	public String token;
	public String client;
	public String lang="en";
	public int userId;
	public String role;
	public String userEmail;
	public String userCode;
	public String ip;
	public String agent;
	public Date accessDateTime;
	public String accessDateTimeString;
	public String baseUrl;
	public String method;
	public String deviceMode;
	public boolean mobile;
	public HttpServletRequest request;
	public Map<String,String> clientAgentDetails;
	
	public SessionInfo() {
		this.accessDateTime=Util.getCurrentDate();
	}
}
