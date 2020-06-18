package com.demo.core.script;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Connection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import com.demo.core.util.Util;
import com.demo.core.util.ZcMap;
import com.mysql.cj.jdbc.MysqlDataSource;

public class ApplyDbScript {
	
	private static final Logger log = LogManager.getLogger(ApplyDbScript.class);
	
	private Connection conn;
	
	private ZcMap connDetails;

	public static final String MYSQL_SCRIPT_FILE="script/dbscript.sql";
	
	public ApplyDbScript(ZcMap connDetails) throws Exception {
		this.connDetails=connDetails;
		log.info("****************** Getting Db Connection ****************");
		log.info("MYSQL_HOST : "+connDetails.getS("host"));
		log.info("MYSQL_PORT : "+connDetails.getS("port"));
		log.info("MYSQL_USER : "+connDetails.getS("user"));
		log.info("MYSQL_PASSWORD : "+connDetails.getS("password"));
		log.info("MYSQL_SCHEMA : "+connDetails.getS("schema"));
		log.info("****************** Getting Db Connection ****************");
		try {
			conn=Util.getConnection(connDetails);
		}catch (Exception e) {
			if(e.getMessage().contains("Unknown database")) {
				createMysqlDbSchemaIfNotExists();
				conn=Util.getConnection(connDetails);
				createDb();
				Util.closeConnection(conn);
			}
		}
		Util.closeConnection(conn);
	}
	public void createDb() throws URISyntaxException, IOException {
		JdbcTemplate jdbcTemplate=new JdbcTemplate(new SingleConnectionDataSource(conn, true));
		log.info("****************** Creating DB Table user ******************************");
		String script="CREATE TABLE `user` (\r\n" + 
				"    `pk_id` INT(11) UNSIGNED NOT NULL AUTO_INCREMENT,\r\n" + 
				"    `name` VARCHAR(255) NULL,\r\n" + 
				"    `email` VARCHAR(160) NULL,\r\n" + 
				"    `password` VARCHAR(60) NULL,\r\n" + 
				"    `phone` VARCHAR(60) NULL,\r\n" + 
				"    PRIMARY KEY (`pk_id`)\r\n" + 
				") ENGINE=InnoDB DEFAULT CHARSET=utf8;\r\n" + 
				"\r\n" + 
				"CREATE TABLE `user_current_logged_session` (\r\n" + 
				"  `pk_id` int(11) unsigned NOT NULL AUTO_INCREMENT,\r\n" + 
				"  `os` varchar(100) DEFAULT NULL,\r\n" + 
				"  `browser` text,\r\n" + 
				"  `ip` varchar(30) DEFAULT NULL,\r\n" + 
				"  `agent` text,\r\n" + 
				"  `session` varchar(100) DEFAULT NULL,\r\n" + 
				"  `token` varchar(1024) DEFAULT NULL,\r\n" + 
				"  `device` varchar(100) DEFAULT NULL,\r\n" + 
				"  `user_id` int(11) unsigned NOT NULL,\r\n" + 
				"  `in_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,\r\n" + 
				"  `fb_token` varchar(2000) DEFAULT NULL COMMENT 'firebase token',\r\n" + 
				"  `expiry` timestamp NULL DEFAULT NULL,\r\n" + 
				"  `last_accessed_on` timestamp NULL DEFAULT NULL,\r\n" + 
				"  `active_time` int(11) DEFAULT NULL,\r\n" + 
				"  `data` text,\r\n" + 
				"  PRIMARY KEY (`pk_id`)\r\n" + 
				") ENGINE=InnoDB DEFAULT CHARSET=utf8;\r\n" + 
				"\r\n" + 
				"CREATE TABLE `access_log` (\r\n" + 
				"  `pk_id` int(11) unsigned NOT NULL AUTO_INCREMENT,\r\n" + 
				"  `url` varchar(1000) NOT NULL,\r\n" + 
				"  `service` varchar(1000) NOT NULL,\r\n" + 
				"  `method_type` varchar(1000) NOT NULL,\r\n" + 
				"  `header` varchar(1000) NOT NULL,\r\n" + 
				"  `req_data` longtext,\r\n" + 
				"  `res_data` longtext,\r\n" + 
				"  `agent` longtext,\r\n" + 
				"  `time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,\r\n" + 
				"  `duration` int(11) DEFAULT NULL,\r\n" + 
				"  `session` varchar(100) DEFAULT NULL,\r\n" + 
				"  `token` varchar(1024) DEFAULT NULL,\r\n" + 
				"  `user_id` int(11) unsigned DEFAULT NULL,\r\n" + 
				"  `ip` varchar(40) NOT NULL,\r\n" + 
				"  `version` varchar(40) DEFAULT NULL,\r\n" + 
				"  PRIMARY KEY (`pk_id`)\r\n" + 
				") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
//		script=Util.getSqlResourceFileAsString(MYSQL_SCRIPT_FILE);
		if(Util.isBlank(script)) return;
		try{
			jdbcTemplate.execute(script);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void createMysqlDbSchemaIfNotExists()throws Exception {
		String host=connDetails.getS("host");
		int port=connDetails.getI("port");
		String user=connDetails.getS("user");
		String password=connDetails.getS("password");
		String schema=connDetails.getS("schema");
		log.info("****************** Creating Schema "+schema+"******************************");
		MysqlDataSource mysqlDs=new MysqlDataSource();
		mysqlDs.setServerName(host);
		mysqlDs.setPort(port); 
		mysqlDs.setUser(user);
		mysqlDs.setPassword(password);
		JdbcTemplate jdbcTemplate=new JdbcTemplate(mysqlDs);
		jdbcTemplate.execute("CREATE SCHEMA IF NOT EXISTS `"+schema+"`  DEFAULT CHARSET=utf8"); 
		if(!mysqlDs.getConnection().isClosed()) mysqlDs.getConnection().close();
	}
}
