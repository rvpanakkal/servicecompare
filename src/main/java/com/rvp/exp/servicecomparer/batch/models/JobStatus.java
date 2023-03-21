package com.rvp.exp.servicecomparer.batch.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JobStatus {

	private long jobId;
	
	private String jobStatus;
}
