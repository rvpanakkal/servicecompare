/**
 * 
 */
package com.rvp.exp.servicecomparer.batch.models.output;

import java.util.List;
import java.util.Map;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * @author U12044
 *
 */
@Data
@Entity
@Table
public class CaseResult {

	@Id
	@GeneratedValue
	private int id;
	
	private String testCaseName;
	
	@ElementCollection(fetch=FetchType.EAGER)
	@CollectionTable(name = "TESTDATA")
	@MapKeyColumn(name = "NAME")
	@Column(name = "TESTVALUE")
	private Map<String, String> testData;
	
	@OneToMany(cascade = {CascadeType.ALL})
	@JoinColumn(name = "caseresult_id")
	private List<Failure> failures;
	
	private Status status;
	
}
