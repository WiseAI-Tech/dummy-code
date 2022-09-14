package com.dummy.code.web.scheduler.jobs;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

public class SampleSchedulerJob {
	@Autowired
	DataSource dataSource;

	@Scheduled(cron = "0 * * * * *")
	public void SampleScheduler() throws Exception {
		/*
		 * This will be called every minute. For more info on cron expressions check on
		 * https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/
		 * springframework/scheduling/support/CronExpression.html
		 */
	}
}
