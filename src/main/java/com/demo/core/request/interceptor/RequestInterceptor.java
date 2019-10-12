package com.demo.core.request.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

//HandlerInterceptorAdapter is class that implements the HandlerInterceptor interface.
/**
 * Class which intercept all Rest Controller Methods
 * 
 * @author Jagadeesh.T
 *
 */
public class RequestInterceptor extends HandlerInterceptorAdapter {
	
	private static final Logger log = LogManager.getLogger(RequestInterceptor.class);
 
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		log.info("Pre Handle");
		String a=10+"";
		System.out.println(a);
		return true;
	}
	 
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		log.info("Post Handle");;
	} 
}