package com.demo.core.scheduler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.demo.core.scheduler.process.SchedulerProcess;

public class DemoJob implements Job{

	private static final Logger log = LogManager.getLogger(DemoJob.class);
	
//	@Autowired
//	private SchedulerProcess schedulerProcess;
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		log.info("Executing");
		SchedulerProcess sp=new SchedulerProcess();
//		ZcMap jobDetails=(ZcMap)context.getJobDetail().getJobDataMap().get("ZcJob-Details");
//		ZcMap jobClient=(ZcMap)context.getJobDetail().getJobDataMap().get("ZcJob-Client");
		sp.sampleProcess();
	}
}
