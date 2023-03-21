/**
 * 
 */
package com.rvp.exp.servicecomparer.batch.processors;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.text.StringSubstitutor;
import org.skyscreamer.jsonassert.FieldComparisonFailure;
import org.skyscreamer.jsonassert.JSONCompare;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.JSONCompareResult;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.rvp.exp.servicecomparer.batch.models.input.ApiDetails;
import com.rvp.exp.servicecomparer.batch.models.input.TestCase;
import com.rvp.exp.servicecomparer.batch.models.output.CaseResult;
import com.rvp.exp.servicecomparer.batch.models.output.Failure;
import com.rvp.exp.servicecomparer.batch.models.output.Result;
import com.rvp.exp.servicecomparer.batch.models.output.Status;

import reactor.core.publisher.Mono;

/**
 * @author U12044
 *
 */
public class ApiCallProcessor implements ItemProcessor<TestCase, Result> {

	private String testSuiteName;
	private long jobId;
	private List<Map<String, String>> testData = null;

	@BeforeStep
	public void beforeStep(StepExecution stepExecution) {
		JobParameters jobParameters = stepExecution.getJobParameters();

		this.testSuiteName = jobParameters.getString("TestSuite");
		this.jobId = stepExecution.getJobExecutionId();

		// this.testData = (List<Map<String, String>>)
		// stepExecution.getJobExecution().getExecutionContext().get("jobTestData");
	}

	@Override
	public Result process(TestCase testCase) throws Exception {

		System.out.println("Executing " + testCase.getName());


		List<Map<String, String>> testData = new ArrayList<Map<String, String>>();
		InputStream inputStream = this.getClass().getClassLoader()
				.getResourceAsStream(this.testSuiteName + "." + testCase.getName() + ".csv");

		CsvMapper mapper = new CsvMapper();
		CsvSchema schema = CsvSchema.emptySchema().withHeader();
		MappingIterator<Map<String, String>> iterator = mapper.reader(Map.class).with(schema).readValues(inputStream);
		while (iterator.hasNext()) {
			testData.add(iterator.next());
		}

		List<CaseResult> caseResults = new ArrayList<>();
		for (Map<String, String> data : testData) {

			CaseResult opc = new CaseResult();
			Mono<String> controlResponse = executeApi(testCase.getControlTestDetails(), data);

			Mono<String> autResponse = executeApi(testCase.getAutTestDetails(), data);

			JSONCompareResult compareResult = JSONCompare.compareJSON(controlResponse.block(), autResponse.block(),
					JSONCompareMode.STRICT);

			List<FieldComparisonFailure> failures = compareResult.getFieldFailures();
			List<Failure> testFailures = new ArrayList<>();

			opc.setStatus(Status.PASS);
			for (FieldComparisonFailure fieldComparisonFailure : failures) {
				System.out.println(fieldComparisonFailure.getField() + " " + fieldComparisonFailure.getExpected());
				System.out.println(fieldComparisonFailure.getField() + " " + fieldComparisonFailure.getActual());
				Failure failure = new Failure();
				failure.setFieldName(fieldComparisonFailure.getField());
				failure.setExpectedValue(fieldComparisonFailure.getExpected().toString());
				failure.setActualValue(fieldComparisonFailure.getActual().toString());
				testFailures.add(failure);
				opc.setStatus(Status.FAIL);
			}

			opc.setTestCaseName(testCase.getName());
			opc.setTestData(data);
			opc.setFailures(testFailures);
			caseResults.add(opc);
		}

		Result res = new Result();
		res.setJobId(this.jobId);
		res.setSuiteName(this.testSuiteName);
		res.setExecutionDate(new Date());
		res.setCaseResult(caseResults);
		return res;
	}

	private Mono<String> executeApi(ApiDetails apiDetails, Map<String, String> data) {

		String replacedUrl = StringSubstitutor.replace(apiDetails.getUrl(), data);
		System.out.println("Executing " + replacedUrl);
		WebClient client = WebClient.builder().baseUrl(replacedUrl)
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).build();

		Mono<String> response = client.get().uri("/").retrieve().bodyToMono(String.class);

		System.out.println(response.block());
		return response;
	}

}
