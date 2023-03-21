/**
 * 
 */
package com.rvp.exp.servicecomparer.batch.models.output;

import java.util.Date;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import lombok.Data;

/**
 * @author U12044
 *
 */
@Data
@Entity
public class Result {

	@Id
	@GeneratedValue
	private long id;
	
	private long jobId;
	
	private String suiteName;
	
	private Date executionDate;
	
	@OneToMany(cascade = {CascadeType.ALL})
	@JoinColumn(name = "result_id")
	private List<CaseResult> caseResult;
}
