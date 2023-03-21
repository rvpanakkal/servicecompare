/**
 * 
 */
package com.rvp.exp.servicecomparer.batch.writers;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Map;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

import com.rvp.exp.servicecomparer.batch.models.output.CaseResult;
import com.rvp.exp.servicecomparer.batch.models.output.Failure;
import com.rvp.exp.servicecomparer.batch.models.output.Result;
import com.rvp.exp.servicecomparer.batch.models.output.Status;

/**
 * @author U12044
 *
 */
public class ExcelFileWriter implements ItemWriter<Result> {

	private String testSuiteName;

	private String outputFileName;

	private SXSSFWorkbook workbook;

	@BeforeStep
	public void beforeStep(StepExecution stepExecution) {

		JobParameters jobParameters = stepExecution.getJobParameters();

		this.testSuiteName = jobParameters.getString("TestSuite");

		String dateTime = DateFormatUtils.format(Calendar.getInstance(), "yyyyMMdd_HHmmss");
		outputFileName = this.testSuiteName + "_" + dateTime + ".xlsx";

		workbook = new SXSSFWorkbook(100);
		Font defaultFont= workbook.createFont();
	    defaultFont.setFontHeightInPoints((short)10);
	    defaultFont.setFontName("Arial");
	    defaultFont.setColor(IndexedColors.BLACK.getIndex());
	    defaultFont.setBold(false);
	    defaultFont.setItalic(false);


		/*
		 * addTitleToSheet(sheet); currRow++; addHeaders(sheet); initDataStyle();
		 */

	}

	@Override
	public void write(Chunk<? extends Result> results) throws Exception {

		for (Result result : results) {
			for (CaseResult caseResult : result.getCaseResult()) {
				Sheet sheet = workbook.getSheet(caseResult.getTestCaseName());
				int currRow = 0;
				if (sheet == null) {
					sheet = workbook.createSheet(caseResult.getTestCaseName());
					sheet.setDefaultColumnWidth(20);
					currRow = addHeaderRows(sheet, caseResult.getTestData());
				} else {
					currRow = sheet.getLastRowNum() + 1;
				}
				if (caseResult.getStatus() == Status.FAIL) {
					Row row = sheet.createRow(currRow);
					createStringCell(row, caseResult.getTestCaseName(), 0);
					Map<String, String> testData = caseResult.getTestData();
					int currCol = 1;
					for (String key : testData.keySet()) {
						createStringCell(row, testData.get(key), currCol);
						currCol++;
					}
					for (Failure failure : caseResult.getFailures()) {
						createStringCell(row, failure.getFieldName(), currCol + 1);
						createStringCell(row, failure.getActualValue(), currCol + 2);
						createStringCell(row, failure.getExpectedValue(), currCol + 3);
					}
				}
				currRow++;
			}
		}
	}

	private int addHeaderRows(Sheet sheet, Map<String, String> testData) {
		
		int currRow = 0;
		
		Font font= workbook.createFont();
	    font.setFontHeightInPoints((short)10);
	    font.setFontName("Arial");
	    font.setColor(IndexedColors.WHITE.getIndex());
	    font.setBold(true);
	    font.setItalic(false);
	    
		Row row = sheet.createRow(currRow);
		CellStyle style=workbook.createCellStyle();
		style.setFont(font);
	    row.setRowStyle(style);
				
		createStringCell(row, "Sl.No", 0);
		int currCol = 1;
		for (String key : testData.keySet()) {
			createStringCell(row, key, currCol);
			currCol++;
		}
		
			createStringCell(row, "Field Name", currCol + 1);
			createStringCell(row, "Actual Value", currCol + 2);
			createStringCell(row, "Expected Value", currCol + 3);
		
		return currRow++;
		
	}

	@AfterStep
	public void afterStep(StepExecution stepExecution) throws IOException {
		FileOutputStream fos = new FileOutputStream(this.outputFileName);
		workbook.write(fos);
		fos.close();
	}

	private void createStringCell(Row row, String val, int col) {
		Cell cell = row.createCell(col);
		cell.setCellValue(val);
	}

}
