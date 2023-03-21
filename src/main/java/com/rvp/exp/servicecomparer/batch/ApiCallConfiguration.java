/**
 * 
 */
package com.rvp.exp.servicecomparer.batch;

import java.util.Map;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.launch.support.TaskExecutorJobLauncher;
import org.springframework.batch.core.listener.ExecutionContextPromotionListener;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import com.rvp.exp.servicecomparer.batch.models.input.TestCase;
import com.rvp.exp.servicecomparer.batch.models.output.Result;
import com.rvp.exp.servicecomparer.batch.processors.ApiCallProcessor;
import com.rvp.exp.servicecomparer.batch.reader.YamlReader;
import com.rvp.exp.servicecomparer.batch.repository.ResultRepository;
import com.rvp.exp.servicecomparer.batch.writers.CustomListWriter;
import com.rvp.exp.servicecomparer.batch.writers.ExcelFileWriter;

/**
 * @author U12044
 *
 */
@Configuration
public class ApiCallConfiguration {

	@Autowired
	ResultRepository resultRepository;

	@Autowired
	JobRepository jobRepository;

	@Bean
	public YamlReader yamlReader() {

		return new YamlReader("prodcompare.yml");
	}

	@Bean
	public ApiCallProcessor apiCallProcessor() {

		return new ApiCallProcessor();
	}

	@Bean
	public ItemWriter<Result> apiCallWriter() {

		/*
		 * Result result -> { System.out.println("Saving results");
		 * resultRepository.saveAll(result); };
		 */
		return new ExcelFileWriter();
	}

	@Bean
	public ItemWriter<Map<String, String>> csvWriter() {

		return new CustomListWriter();
	}

	@Bean
	public Job apiComparisonJob(JobRepository jobRepository, JobCompletionNotificationListener listener,
			Step executeApiCall, Step readTestData) {
		return new JobBuilder("apiComparisonJob", jobRepository).incrementer(new RunIdIncrementer()).listener(listener)
				.start(executeApiCall).build();
	}

	@Bean
	public Step executeApiCall(JobRepository jobRepository, PlatformTransactionManager transactionManager,
			ItemWriter<Result> writer) {
		return new StepBuilder("executeApiCall", jobRepository).<TestCase, Result>chunk(10, transactionManager)
				.reader(yamlReader()).processor(apiCallProcessor()).writer(apiCallWriter()).allowStartIfComplete(true)
				.listener(promotionListener()).allowStartIfComplete(true).build();
	}

	@Bean
	public ExecutionContextPromotionListener promotionListener() {
		ExecutionContextPromotionListener listener = new ExecutionContextPromotionListener();
		listener.setKeys(new String[] { "count" });
		return listener;
	}
	
	@Bean
	public JobLauncher apiJobLauncher() throws Exception {
		TaskExecutorJobLauncher jobLauncher = new TaskExecutorJobLauncher();
		jobLauncher.setJobRepository(jobRepository);
		jobLauncher.setTaskExecutor(new SimpleAsyncTaskExecutor());
		jobLauncher.afterPropertiesSet();
		return jobLauncher;
	}

}
