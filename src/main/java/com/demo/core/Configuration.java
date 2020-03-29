package com.demo.core;

import java.util.Properties;
import javax.sql.DataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import com.demo.core.constants.Constants;
import com.demo.core.scheduler.DemoJob;
import com.demo.core.util.Util;



@org.springframework.context.annotation.Configuration
@EnableScheduling
@EnableAsync
@PropertySource(value = "classpath:spring-boot-demo.properties",ignoreResourceNotFound = true)
public class Configuration {

	private static final Logger log = LogManager.getLogger(Configuration.class);
	
	public static Scheduler scheduler;
	
	@Autowired
	Environment env;
	
	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}
	
	@Bean
	public Constants constants(@Value("${MYSQL_HOST:}") String mysqlHost,
								@Value("${MYSQL_PORT:}") Integer mysqlPort,
								@Value("${MYSQL_USER:}") String mysqlUser,
								@Value("${MYSQL_PASSWORD:}") String mysqlPassword,
								@Value("${MYSQL_SCHEMA:}") String mysqlSchema) {
		if(Util.hasData(mysqlHost)) Constants.MYSQL_HOST=mysqlHost;
		if(Util.hasData(mysqlPort)) Constants.MYSQL_PORT=mysqlPort;
		if(Util.hasData(mysqlUser)) Constants.MYSQL_USER=mysqlUser;
		if(Util.hasData(mysqlPassword)) Constants.MYSQL_PASSWORD=mysqlPassword;
		return new Constants();
		
	}
	
	@Bean
	@DependsOn("constants")
	public DataSource dataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("com.mysql.jdbc.Driver");
	    dataSource.setUrl("jdbc:mysql://"+Constants.MYSQL_HOST+":"+Constants.MYSQL_PORT+"/"+Constants.MYSQL_SCHEMA);
	    dataSource.setUsername(Constants.MYSQL_USER);
	    dataSource.setPassword(Constants.MYSQL_PASSWORD);
	    return dataSource;
	}
	
	@Bean
	@DependsOn("dataSource")
	public JdbcTemplate JdbcTemplate(DataSource dataSorce) {
		return new JdbcTemplate(dataSorce);
	}
	
	@Bean
	public Scheduler scheduler()throws Exception {
		Properties quartzProperties=new Properties();
		quartzProperties.put("org.quartz.scheduler.instanceId","AUTO");
		quartzProperties.put("org.quartz.scheduler.skipUpdateCheck","true");
		quartzProperties.put("org.quartz.scheduler.rmi.export", "false");
		quartzProperties.put("org.quartz.scheduler.rmi.proxy", "false");
		quartzProperties.put("org.quartz.scheduler.wrapJobExecutionInUserTransaction", "false");
		quartzProperties.put("org.quartz.scheduler.interruptJobsOnShutdownWithWait", "true");
		quartzProperties.put("org.quartz.threadPool.class", "org.quartz.simpl.SimpleThreadPool");
		quartzProperties.put("org.quartz.threadPool.threadCount", "10");
		quartzProperties.put("org.quartz.threadPool.threadPriority", "5");
		quartzProperties.put("org.quartz.threadPool.threadsInheritContextClassLoaderOfInitializingThread", "true");
		quartzProperties.put("org.quartz.jobStore.misfireThreshold", "60000");
		quartzProperties.put("org.quartz.jobStore.class", "org.quartz.simpl.RAMJobStore");
		scheduler=new StdSchedulerFactory(quartzProperties).getScheduler();
	    return scheduler;
	} 
	
	@Bean
	@DependsOn({"scheduler"})
	public JobDetail jobDetail() {
		log.info("JobDetail");
	    return JobBuilder.newJob().ofType(DemoJob.class)
		      .storeDurably()
		      .withIdentity("Sample")  
		      .withDescription("Invoke Sample Job service...")
		      .build();
	}
	
	@Bean
	public Trigger jobATrigger(JobDetail jobDetail) throws SchedulerException {
		Trigger t= TriggerBuilder.newTrigger().forJob(jobDetail).withIdentity("sampleTriggerA")
				.withSchedule(CronScheduleBuilder.cronSchedule("0 * * * * ?")).build();
		scheduler.scheduleJob(jobDetail,t);
		scheduler.start();
		return t;
	}
}
