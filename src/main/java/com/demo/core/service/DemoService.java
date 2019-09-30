package com.demo.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.demo.core.dao.DemoDao;
import com.demo.core.util.ZcMap;

@Service (value = "demoService")
public class DemoService {

	@Autowired
	private DemoDao demoDao;
	
	public ZcMap checkDbConnection() {
		return demoDao.checkDbConnection();
	}

}
