package com.demo.core;

import java.util.Properties;

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
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.demo.core.scheduler.DemoJob;



@org.springframework.context.annotation.Configuration
@EnableScheduling
@EnableAsync
public class Configuration {

	private static final Logger log = LogManager.getLogger(Configuration.class);
	
	public static Scheduler scheduler;
	
	
	@Bean
	@Primary
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
