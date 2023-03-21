/**
 * 
 */
package com.rvp.exp.servicecomparer.batch.models.input;

import java.util.List;
import java.util.Map;

import lombok.Data;

/**
 * @author U12044
 *
 */
@Data
public class ApiDetails {

	private String url;
	
	private String httpMethod;
	
	private Map<String, String> headers;
	
}
