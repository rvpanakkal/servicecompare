/**
 * 
 */
package com.rvp.exp.servicecomparer.batch.models.input;

import lombok.Data;

/**
 * @author U12044
 *
 */
@Data
public class TestCase {

	private int id;
	
	private String name;
	
	private ApiDetails controlTestDetails;
	
	private ApiDetails autTestDetails;
}
