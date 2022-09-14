package com.dummy.code.web.scheduler;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.dummy.code.web.scheduler.jobs.SampleSchedulerJob;

@Configuration
@EnableScheduling
public class SchedulerManager {
	@Bean
	@ConditionalOnProperty(value = "scheduler.allow-sample-scheduler-job", matchIfMissing = false, havingValue = "true")
	public SampleSchedulerJob sampleSchedulerJob() {
		return new SampleSchedulerJob();
	}
}