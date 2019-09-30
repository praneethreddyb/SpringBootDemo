package com.demo.core.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.demo.core.util.ZcMap;

@Repository(value = "demoDao")
public class DemoDao {

	@Autowired
	protected JdbcTemplate jdbcTemplate;
	
	@SuppressWarnings("serial")
	public ZcMap checkDbConnection() {
		return new ZcMap() {{
			put("message", jdbcTemplate==null?"Not connected":"Connected");
			put("data", jdbcTemplate.queryForList("SELECT * FROM app_user;"));
		}};
	}

}
