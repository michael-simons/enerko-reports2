package de.enerko.reports2;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;

import org.junit.Test;

/**
 * @author Michael J. Simons, 2013-06-18
 */
public class ReportEngineTest extends AbstractDatabaseTest {	
	@Test
	public void shouldHandleValidStatements() throws IOException {
		final ReportEngine reportEngine = new ReportEngine(connection);
		
		final Report report = reportEngine.createReport("Select 's1' as sheetname, 1 as cell_column, 1 as cell_row, 'c1' as cell_name, 'string' as cell_type, 'cv' as cell_value from dual");
		
		File outFile = File.createTempFile(ReportEngineTest.class.getSimpleName() + "-", ".xls");
		report.write(new BufferedOutputStream(new FileOutputStream(outFile)));
		
		ReportEngine.logger.log(Level.INFO, String.format("Report written to %s", outFile.getAbsolutePath()));
	}
	
	@Test
	public void shouldHandleValidFunctions() throws IOException {
		final ReportEngine reportEngine = new ReportEngine(connection);
		
		final Report report = reportEngine.createReport("pck_enerko_reports2_test.f_fb_report_source_test", "5", "21.09.1979", "test");
		
		File outFile = File.createTempFile(ReportEngineTest.class.getSimpleName() + "-", ".xls");		
		report.write(new BufferedOutputStream(new FileOutputStream(outFile)));
		
		ReportEngine.logger.log(Level.INFO, String.format("Report written to %s", outFile.getAbsolutePath()));
	}
	
	@Test
	public void shouldHandleTempaltes() throws IOException {
		final ReportEngine reportEngine = new ReportEngine(connection);
		
		final InputStream template = this.getClass().getResource("/template1.xls").openStream();
				
		final Report report = reportEngine.createReport("pck_enerko_reports2_test.f_fb_report_source_test", template, "5", "21.09.1979", "test");
		
		File outFile = File.createTempFile(ReportEngineTest.class.getSimpleName() + "-", ".xls");		
		report.write(new BufferedOutputStream(new FileOutputStream(outFile)));
		
		ReportEngine.logger.log(Level.INFO, String.format("Report written to %s", outFile.getAbsolutePath()));		
	}
	
	@Test
	public void displayAllFeatures() throws IOException {
		final ReportEngine reportEngine = new ReportEngine(connection);
				
		final Report report = reportEngine.createReport("pck_enerko_reports2_test.f_all_features", new String[0]);
		
		File outFile = File.createTempFile(ReportEngineTest.class.getSimpleName() + "-", ".xls");		
		report.write(new BufferedOutputStream(new FileOutputStream(outFile)));
		
		ReportEngine.logger.log(Level.INFO, String.format("Report written to %s", outFile.getAbsolutePath()));		
	}
}