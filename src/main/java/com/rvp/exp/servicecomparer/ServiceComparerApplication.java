package com.rvp.exp.servicecomparer;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableBatchProcessing
@SpringBootApplication
public class ServiceComparerApplication implements CommandLineRunner {

	@Autowired
	JobLauncher jobLauncher;

	@Autowired
	Job job;

	public static void main(String[] args) {
		
		System.exit(SpringApplication.exit(SpringApplication.run(ServiceComparerApplication.class, args)));
	}

	@Override
	public void run(String... args) throws Exception {
		
		System.out.println(args[0]);
		JobParameters jobParameters = new JobParametersBuilder().addString("TestSuite", args[0], false)
				.toJobParameters();

		JobExecution execution = jobLauncher.run(job, jobParameters);
		System.out.println("STATUS :: " + execution.getStatus());

	}

}
