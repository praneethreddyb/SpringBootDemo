package com.demo.core.scheduler;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks {
  
	private static final Logger log = LogManager.getLogger(ScheduledTasks.class);
	
	private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

//    @Scheduled(fixedRate = 2000)
//    public void scheduleTaskWithFixedRate() {
//    	log.info("Current Thread : {}", Thread.currentThread().getName());
//    	log.info("Fixed Rate:"+dateTimeFormatter.format(LocalDateTime.now()));
//    }

//    @Scheduled(fixedDelay = 2000)
//    public void scheduleTaskWithFixedDelay() {
//    	log.info("Current Thread : {}", Thread.currentThread().getName());
//    	log.info("Fixed Delay:"+dateTimeFormatter.format(LocalDateTime.now()));
//    }
//
//    @Scheduled(fixedRate = 2000, initialDelay = 5000)
//    public void scheduleTaskWithInitialDelay() {
//    	log.info("Current Thread : {}", Thread.currentThread().getName());
//    	log.info("Fixed Rate And Fixed Delay:"+dateTimeFormatter.format(LocalDateTime.now()));
//    }
//
    @Scheduled(cron = "0 * * * * ?")
    public void scheduleTaskWithCronExpression() {
    	log.info("Current Thread : {}", Thread.currentThread().getName());
    	log.info("Cron Task :"+dateTimeFormatter.format(LocalDateTime.now()));
    }
}