/**
 * 
 */
package com.rvp.exp.servicecomparer.batch.models.input;

import java.util.List;

import lombok.Data;

/**
 * @author U12044
 *
 */
@Data
public class TestSuite {

	private String id;
	
	private String name;
	
	private List<TestCase> testCases;
}
