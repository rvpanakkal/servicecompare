/**
 * 
 */
package com.rvp.exp.servicecomparer.batch.models.output;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * @author U12044
 *
 */
@Data
@Entity
@Table
public class Failure {
	
	public Failure() {
		
	}

	@Id
	@GeneratedValue
	private long Id;
	
	private String fieldName;
	
	private String expectedValue;
	
	private String actualValue;
	
}
