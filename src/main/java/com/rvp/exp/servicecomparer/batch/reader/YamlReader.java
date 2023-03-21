/**
 * 
 */
package com.rvp.exp.servicecomparer.batch.reader;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.support.AbstractItemCountingItemStreamItemReader;
import org.springframework.batch.item.support.IteratorItemReader;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import com.rvp.exp.servicecomparer.batch.models.input.Meta;
import com.rvp.exp.servicecomparer.batch.models.input.TestCase;

/**
 * @author U12044
 *
 */
public class YamlReader extends AbstractItemCountingItemStreamItemReader<TestCase> {

	private IteratorItemReader<TestCase> testCaseList;

	private String resourceName;

	private String testSuiteName = null;

	public YamlReader() {

		this.resourceName = "data.yaml";
	}

	public YamlReader(String resourceName) {

		this.resourceName = resourceName;
	}

	@BeforeStep
	public void beforeStep(StepExecution stepExecution) {
		JobParameters jobParameters = stepExecution.getJobParameters();

		this.testSuiteName = jobParameters.getString("TestSuite");
	}

	@Override
	protected TestCase doRead() throws Exception {

		TestCase testCase = testCaseList.read();

		/*
		 * if (testCase == null) { fetchRecs(); testCase = testCaseList.read(); }
		 */

		return testCase;
	}

	protected void fetchRecs() throws Exception {
		List<TestCase> testCases = getTestEnvData(getName(), getName());

		// Store the items in an ItemReader used in the doRead() method
		testCaseList = new IteratorItemReader<TestCase>(testCases);
	}

	private List<TestCase> getTestEnvData(String scenarioName, String testName) {

		System.out.println(this.testSuiteName);
		Yaml yaml = new Yaml(new Constructor(Meta.class));
		List<TestCase> filteredTestCases = new ArrayList<TestCase>();

		InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(resourceName);

		Iterable<Object> objects = yaml.loadAll(inputStream);

		for (Object object : objects) {
			Meta meta = (Meta) object;
			if (meta.getTestSuite().getName().equalsIgnoreCase(this.testSuiteName)) {
				filteredTestCases.addAll(
						meta.getTestSuite().getTestCases().stream().filter(tc -> true).collect(Collectors.toList()));
			}
		}
		return filteredTestCases;
	}

	@Override
	protected void doOpen() throws Exception {

		fetchRecs();
		setName(YamlReader.class.getName());

	}

	@Override
	protected void doClose() throws Exception {
		
	}

}
