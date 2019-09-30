package com.demo.core.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.demo.core.base.controller.BaseController;
import com.demo.core.response.entity.ResponseEntity;
import com.demo.core.service.DemoService;
import com.demo.core.util.ZcMap;


@RestController
@RequestMapping(path = "/demo")
public class DemoController extends BaseController{
	
	@Autowired
	private  DemoService demodService;
	
	public DemoController(HttpServletRequest request) {
		super(request);
	}

	@SuppressWarnings("serial")
	@RequestMapping(path = "/{message}" , method = RequestMethod.GET)
	public ResponseEntity demoService(@PathVariable String message){
		return new ResponseEntity(sessionInfo.request,new ZcMap(){{
			put("success", true);
			put("message", message);
		}}) ;
	}
	
	@RequestMapping(path = "/checkDbConnection" , method = RequestMethod.GET)
	public ResponseEntity checkDbConnection(){
		return new ResponseEntity(sessionInfo.request,demodService.checkDbConnection()) ;
	}
}
