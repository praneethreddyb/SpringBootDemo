package com.demo.core.scheduler.process;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class SchedulerProcess {

	private static final Logger log = LogManager.getLogger(SchedulerProcess.class);
	
	public void sampleProcess() {
		log.info("***************sample Process******************");
	}
}
