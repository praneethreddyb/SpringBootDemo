package com.demo.core.request.interceptor;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;


 
@javax.servlet.annotation.WebFilter(urlPatterns = {"/*"})
@Component
public class WebFilter implements Filter{

	private static final Logger log = LogManager.getLogger(WebFilter.class);
	
	private String encoding;
	
	@Override
	public void destroy() {	}
	
	@Override
	public void init(FilterConfig config) throws ServletException {
		log.info("init");
        encoding = config.getInitParameter("requestEncoding");
        if (encoding == null) encoding = "UTF-8";
    }
 
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		log.info("Do Filter");
		chain.doFilter(request, response);
	}

}
