/**
 * 
 */
package com.rvp.exp.servicecomparer.batch.writers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

/**
 * @author U12044
 *
 */
public class CustomListWriter implements ItemWriter<Map<String, String>>, StepExecutionListener {

	private List<Map<String, String>> dataList = new ArrayList<Map<String, String>>();
	
	@Override
	public void write(Chunk<? extends Map<String, String>> chunk) throws Exception {
		
		for (Map<String, String> data : chunk) {
			
			dataList.add(data);
		}
		
	}

	@AfterStep
	public void persistData(StepExecution stepExecution) {
		
		stepExecution.getExecutionContext().put("jobTestData", dataList);
	}
	

}