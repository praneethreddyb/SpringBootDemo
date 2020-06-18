package com.demo.core;

import java.time.Duration;
import java.util.Properties;
import javax.sql.DataSource;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration.JedisClientConfigurationBuilder;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import com.demo.core.constants.Constants;
import com.demo.core.scheduler.DemoJob;
import com.demo.core.script.ApplyDbScript;
import com.demo.core.util.Util;
import com.demo.core.util.ZcMap;



@org.springframework.context.annotation.Configuration
@EnableScheduling
@EnableAsync
@PropertySource(value = "classpath:spring-boot-demo.properties",ignoreResourceNotFound = true)
public class Configuration implements WebMvcConfigurer{

	private static final Logger log = LogManager.getLogger(Configuration.class);
	
	public static Scheduler scheduler;
	
	@Autowired
	Environment env;
	
	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}
	
	@Bean
	@DependsOn({"propertySourcesPlaceholderConfigurer"})
	public Constants constants(@Value("${MYSQL_HOST:}") String mysqlHost,
								@Value("${MYSQL_PORT:}") Integer mysqlPort,
								@Value("${MYSQL_USER:}") String mysqlUser,
								@Value("${MYSQL_PASSWORD:}") String mysqlPassword,
								@Value("${MYSQL_SCHEMA:}") String mysqlSchema,
								@Value("${ENABLE_CORS:true}") boolean enableCors) {
		if(Util.hasData(mysqlHost)) Constants.MYSQL_HOST=mysqlHost;
		if(Util.hasData(mysqlPort)) Constants.MYSQL_PORT=mysqlPort;
		if(Util.hasData(mysqlUser)) Constants.MYSQL_USER=mysqlUser;
		if(Util.hasData(mysqlPassword)) Constants.MYSQL_PASSWORD=mysqlPassword;
		if(Util.hasData(enableCors)) Constants.ENABLE_CORS=enableCors;
		return new Constants();
		
	}
	
	@SuppressWarnings("serial")
	@Bean
	@DependsOn("constants")
	public ApplyDbScript applyDbScript() throws Exception {
		log.info("************** Applying Db Script **************");
		return new ApplyDbScript(new ZcMap() {{put("schema", Constants.MYSQL_SCHEMA);
												put("host", Constants.MYSQL_HOST);
												put("user", Constants.MYSQL_USER);
												put("password", Constants.MYSQL_PASSWORD);
												put("port", Constants.MYSQL_PORT);}});
	}
	
	@Bean
	@DependsOn("applyDbScript")
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
	public JdbcTemplate jdbcTemplate(DataSource dataSorce) {
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
		Trigger trigger= TriggerBuilder.newTrigger().forJob(jobDetail).withIdentity("sampleTriggerA")
								 .withSchedule(CronScheduleBuilder.cronSchedule("0 * * * * ?")).build();
		scheduler.scheduleJob(jobDetail,trigger);
		scheduler.start();
		return trigger;
	}
	
	@Override
    public void addCorsMappings(CorsRegistry registry) {
		if(Constants.ENABLE_CORS) {
			registry.addMapping("/**").allowedMethods("*").allowedHeaders("*").allowCredentials(true).maxAge(3600);
		}
    }
	
	@SuppressWarnings("rawtypes")
	@Bean
	@DependsOn("jdbcTemplate")
	JedisConnectionFactory jedisConnectionFactory() {
		log.info("*********************** Redis Conncection Details ***********************");
		log.info("Redis Host Name " + Constants.REDIS_HOST);
		log.info("Redis Auth : " + Constants.REDIS_AUTH);
		log.info("Redis Port : " + Constants.REDIS_PORT);
		log.info("Redis DB : " + Constants.REDIS_DB);
		log.info("*********************** Redis Conncection Details ***********************");
		RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
		redisStandaloneConfiguration.setHostName(Constants.REDIS_HOST);
		redisStandaloneConfiguration.setPort(Constants.REDIS_PORT);
		if(Util.isBlank(Constants.REDIS_AUTH))
			redisStandaloneConfiguration.setPassword(RedisPassword.none());
		else 
			redisStandaloneConfiguration.setPassword(RedisPassword.of(Constants.REDIS_AUTH));
		redisStandaloneConfiguration.setDatabase(Util.parseInt(Constants.REDIS_DB));
		JedisClientConfigurationBuilder jedisClientConfiguration = JedisClientConfiguration.builder();
		jedisClientConfiguration.readTimeout(Duration.ofSeconds(180));
		GenericObjectPoolConfig poolConfig=new GenericObjectPoolConfig();
			poolConfig.setTestOnBorrow(true);
			poolConfig.setMaxWaitMillis(1000*60);
			poolConfig.setMaxIdle(32);
			poolConfig.setMinIdle(16);
			poolConfig.setMaxTotal(Integer.MAX_VALUE);  
		jedisClientConfiguration.usePooling().poolConfig(poolConfig);
		jedisClientConfiguration.connectTimeout(Duration.ofSeconds(180));// 60s connection timeout
		jedisClientConfiguration.readTimeout(Duration.ofSeconds(180));
		JedisConnectionFactory jedisConFactory = new JedisConnectionFactory(redisStandaloneConfiguration,jedisClientConfiguration.build());
		return jedisConFactory;
	}
	@Bean
	@DependsOn("jedisConnectionFactory")
	CacheManager cacheManager() {
		RedisSerializationContext.SerializationPair<Object> jsonSerializer = RedisSerializationContext.SerializationPair
				.fromSerializer(new GenericJackson2JsonRedisSerializer());
		return RedisCacheManager.RedisCacheManagerBuilder.fromConnectionFactory(jedisConnectionFactory())
				.cacheDefaults(RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ZERO)//ofDays(1)
						.serializeValuesWith(jsonSerializer))
				.build();
	} 
}
